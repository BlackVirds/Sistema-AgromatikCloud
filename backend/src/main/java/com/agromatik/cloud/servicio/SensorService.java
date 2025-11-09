package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.SensorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.agromatik.cloud.util.ValidacionesUtil.validarUbicacion;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final HuertaRepository huertaRepository;

    public SensorService(SensorRepository sensorRepository,  HuertaRepository huertaRepository) {
        this.sensorRepository = sensorRepository;
        this.huertaRepository = huertaRepository;
    }

    /**
     * Devuelve todos los sensores (si es Admin) o solo los del usuario.
     */
    public List<Sensor> getAll(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (esAdmin) {
            return sensorRepository.findAll();
        } else {
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            return sensorRepository.findByHuertaUsuarioEmail(emailUsuario);
        }
    }


    public Optional<Sensor> getByUuid(String uuid){
        return sensorRepository.findByUuid(uuid);
    }

    /**
     *Obtiene sensores de UNA huerta, validando que la huerta sea del usuario.
     */
    public List<Sensor> getByHuerta(Long huertaId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (esAdmin) {
            return sensorRepository.findByHuertaId(huertaId);
        } else {
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            return sensorRepository.findByHuertaIdAndHuertaUsuarioEmail(huertaId, emailUsuario);
        }
    }

    public Sensor save(Sensor sensor){

        Long huertaId = sensor.getHuerta().getId();
        if (huertaId == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear un sensor.");
        }
        // 🔹 Validar ubicación solo si viene con valor real
        if (sensor.getUbicacionGeografica() != null && !sensor.getUbicacionGeografica().isBlank()) {
            validarUbicacion(sensor.getUbicacionGeografica());
        } else if (sensor.getUbicacionGeografica() != null && sensor.getUbicacionGeografica().isBlank()) {
            // 🔹 Si el cliente envía "", limpiamos el valor
            sensor.setUbicacionGeografica(null);
        }

        Huerta huertaExistente = huertaRepository.findById(huertaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + huertaId));

        sensor.setHuerta(huertaExistente);
        return sensorRepository.save(sensor);
    }

    public Optional<Sensor> updateSensor(String uuid, Sensor updatedSensor) {
        return sensorRepository.findByUuid(uuid).map(sensor -> {


            if (updatedSensor.getNombre() != null) {
                sensor.setNombre(updatedSensor.getNombre());
            }
            if (updatedSensor.getTipoSensor() != null) {
                sensor.setTipoSensor(updatedSensor.getTipoSensor());
            }
            if (updatedSensor.getFabricante() != null) {
                sensor.setFabricante(updatedSensor.getFabricante());
            }
            //  Manejo completo de la ubicación geográfica (opcional + validación)
            if (updatedSensor.getUbicacionGeografica() != null && !updatedSensor.getUbicacionGeografica().isBlank()) {
                validarUbicacion(updatedSensor.getUbicacionGeografica());
                sensor.setUbicacionGeografica(updatedSensor.getUbicacionGeografica());
            } else if (updatedSensor.getUbicacionGeografica() != null && updatedSensor.getUbicacionGeografica().isBlank()) {
                // Si el cliente manda una cadena vacía, limpiamos el campo
                sensor.setUbicacionGeografica(null);
            }
            if (updatedSensor.getFechaInstalacion() != null) {
                sensor.setFechaInstalacion(updatedSensor.getFechaInstalacion());
            }
            if (updatedSensor.getUltimoMantenimiento() != null) {
                sensor.setUltimoMantenimiento(updatedSensor.getUltimoMantenimiento());
            }
            if (updatedSensor.getEstado() != null) {
                sensor.setEstado(updatedSensor.getEstado());
            }
            if (updatedSensor.getBateriaNivel() != null) {
                sensor.setBateriaNivel(updatedSensor.getBateriaNivel());
            }
            if (updatedSensor.getConfiguraciones() != null) {
                sensor.setConfiguraciones(updatedSensor.getConfiguraciones());
            }
            if (updatedSensor.getModelo() != null) {
                sensor.setModelo(updatedSensor.getModelo());
            }
            if (updatedSensor.getHuerta() != null && updatedSensor.getHuerta().getId() != null) {
                Long nuevaHuertaId = updatedSensor.getHuerta().getId();

                Huerta huertaExistente = huertaRepository.findById(nuevaHuertaId)
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + nuevaHuertaId));

                sensor.setHuerta(huertaExistente);

            } else if (updatedSensor.getHuerta() != null) {
                throw new IllegalArgumentException("Se intentó actualizar la huerta pero no se proporcionó un ID de huerta.");
            }

            return sensorRepository.save(sensor);
        });
    }

    public boolean delete(String uuid) {
        return sensorRepository.findByUuid(uuid).map(sensor -> {

            // Si el sensor ya está inactivo o fallido, consideramos el borrado lógico exitoso
            if (sensor.getEstado() == Sensor.EstadoSensor.INACTIVO || sensor.getEstado() == Sensor.EstadoSensor.FALLA) {
                return true;
            }

            //Aplicamos el Soft Delete: Cambiamos el estado a 'inactivo'
            sensor.setEstado(Sensor.EstadoSensor.INACTIVO);

            //Guardamos el cambio (el registro se mantiene en la BD)
            sensorRepository.save(sensor);

            return true;
        }).orElse(false); // Si no se encuentra el UUID, devuelve false (404 Not Found)
    }
}
