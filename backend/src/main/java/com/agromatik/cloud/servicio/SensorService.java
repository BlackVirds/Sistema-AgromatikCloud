package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
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
            if (updatedSensor.getHuerta() != null) {
                sensor.setHuerta(updatedSensor.getHuerta());
            }

            return sensorRepository.save(sensor);
        });
    }

    public void delete(String uuid){
        sensorRepository.findByUuid(uuid).ifPresent(sensorRepository::delete);
    }
}
