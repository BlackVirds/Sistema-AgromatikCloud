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

            if (updatedSensor.getTipo() != null) {
                sensor.setTipo(updatedSensor.getTipo());
            }
            if (updatedSensor.getModelo() != null) {
                sensor.setModelo(updatedSensor.getModelo());
            }
            if (updatedSensor.getDescripcion() != null) {
                sensor.setDescripcion(updatedSensor.getDescripcion());
            }
            if (updatedSensor.getUbicacion() != null) {
                sensor.setUbicacion(updatedSensor.getUbicacion());
            }
            if (updatedSensor.getActivo() != null) {
                sensor.setActivo(updatedSensor.getActivo());
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
