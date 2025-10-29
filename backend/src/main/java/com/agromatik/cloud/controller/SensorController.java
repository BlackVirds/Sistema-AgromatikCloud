package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.SensorRepository;
import com.agromatik.cloud.servicio.SensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensores")
@CrossOrigin(origins = "*")
public class SensorController {
    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public List<Sensor> getAllSensores(){
        return sensorService.getAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Sensor> getSensorByUuid(@PathVariable String uuid){
        return sensorService.getByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @GetMapping("/huerta/{huertaId}")
    public List<Sensor> getSensorByHuerta(@PathVariable Long huertaId){
        return sensorService.getByHuerta(huertaId);
    }

    @PostMapping
    public Sensor createSensor(@RequestBody Sensor sensor){
        return sensorService.save(sensor);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteSensor(@PathVariable String uuid){
        sensorService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Sensor> updateSensor(@PathVariable String uuid, @RequestBody Sensor updatedSensor){
        return sensorService.updateSensor(uuid, updatedSensor)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
