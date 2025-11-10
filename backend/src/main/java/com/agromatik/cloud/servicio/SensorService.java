package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.SensorRepository;
import com.agromatik.cloud.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    public SensorService(SensorRepository sensorRepository, HuertaRepository huertaRepository, UsuarioRepository usuarioRepository) {
        this.sensorRepository = sensorRepository;
        this.huertaRepository = huertaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Métodos Auxiliares de Seguridad ---

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private String getEmailUsuario(Authentication authentication) {
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
    /**
     * Devuelve todos los sensores (si es Admin) o solo los del usuario.
     */
    // --- MÉTODOS DE CONSULTA (GET) SEGUROS ---

    public List<Sensor> getAll(){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return sensorRepository.findAll();
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return sensorRepository.findByHuertaUsuarioEmail(emailUsuario);
        }
    }

    /**
     * Busca un sensor por UUID, solo si eres Admin o si te pertenece.
     */
    public Optional<Sensor> getByUuid(String uuid){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return sensorRepository.findByUuid(uuid);
        } else {
            String email = getEmailUsuario(auth);
            return sensorRepository.findByUuidAndHuertaUsuarioEmail(uuid, email);
        }
    }
    /**
     *Obtiene sensores de UNA huerta, validando que la huerta sea del usuario.
     */
    public List<Sensor> getByHuerta(Long huertaId){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return sensorRepository.findByHuertaId(huertaId);
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return sensorRepository.findByHuertaIdAndHuertaUsuarioEmail(huertaId, emailUsuario);
        }
    }

    /**
     * Valida que la huerta (del JSON) pertenezca al usuario (del token).
     */
    public Sensor save(Sensor sensor){
        Authentication auth = getAuthentication();
        Long huertaId = sensor.getHuerta().getId();

        if (huertaId == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear un sensor.");
        }

        // 1. Validar que la huerta exista (Usando el método estándar)
        Huerta huertaExistente = huertaRepository.findById(huertaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + huertaId));

        // 2. Validar Pertenencia
        if (!esAdmin(auth)) {
            String emailUsuarioToken = getEmailUsuario(auth);
            Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioToken)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

            if (!huertaExistente.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                throw new SecurityException("Acceso denegado. No tiene permiso para crear sensores en esta huerta.");
            }
        }

        // 3. Asignación segura y validación de ubicación
        sensor.setHuerta(huertaExistente);
        if (sensor.getUbicacionGeografica() != null && !sensor.getUbicacionGeografica().isBlank()) {
            validarUbicacion(sensor.getUbicacionGeografica());
        }

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
            // Lógica de Re-asignación de Huerta (SEGURA)
            if (updatedSensor.getHuerta() != null && updatedSensor.getHuerta().getId() != null) {
                Authentication auth = getAuthentication();
                Long nuevaHuertaId = updatedSensor.getHuerta().getId();
                Huerta nuevaHuerta = huertaRepository.findById(nuevaHuertaId)
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + nuevaHuertaId));

                if (!esAdmin(auth)) {
                    String emailUsuarioToken = getEmailUsuario(auth);
                    if (!nuevaHuerta.getUsuario().getEmail().equals(emailUsuarioToken)) {
                        throw new SecurityException("Acceso denegado. No tiene permiso para asignar este sensor a esa huerta.");
                    }
                }
                sensor.setHuerta(nuevaHuerta);
            }

            return sensorRepository.save(sensor);
        });
    }

    /**
     * Desactiva un sensor, solo si eres Admin o si te pertenece.
     */
    public boolean delete(String uuid) {
        // Usa getByUuid (que ya es seguro) para encontrar el sensor
        return this.getByUuid(uuid).map(sensor -> {
            if (sensor.getEstado() == Sensor.EstadoSensor.INACTIVO || sensor.getEstado() == Sensor.EstadoSensor.FALLA) {
                return true;
            }
            sensor.setEstado(Sensor.EstadoSensor.INACTIVO);
            sensorRepository.save(sensor);
            return true;
        }).orElse(false);
    }
}
