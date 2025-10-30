package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.SensorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final HuertaRepository huertaRepository;

    public SensorService(SensorRepository sensorRepository,  HuertaRepository huertaRepository) {
        this.sensorRepository = sensorRepository;
        this.huertaRepository = huertaRepository;
    }

    public List<Sensor> getAll(){
        return sensorRepository.findAll();
    }

    public Optional<Sensor> getByUuid(String uuid){
        return sensorRepository.findByUuid(uuid);
    }

    public List<Sensor>getByHuerta(Long huertaId){
        return sensorRepository.findByHuertaId(huertaId);
    }

    public Sensor save(Sensor sensor){

        Long huertaId = sensor.getHuerta().getId();
        if (huertaId == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear un sensor.");
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
            if (updatedSensor.getUbicacionGeografica() != null) {
                sensor.setUbicacionGeografica(updatedSensor.getUbicacionGeografica());
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
            sensorRepository.delete(sensor);
            return true;
        }).orElse(false);
    }
}
