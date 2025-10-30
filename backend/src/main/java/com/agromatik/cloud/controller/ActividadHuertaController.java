package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.ActividadHuerta;
import com.agromatik.cloud.servicio.ActividadHuertaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actividades-huerta")
@CrossOrigin(origins = "*")
public class ActividadHuertaController {
    private final ActividadHuertaService actividadService;

    public ActividadHuertaController(ActividadHuertaService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public List<ActividadHuerta> getAllActividades() {
        return actividadService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadHuerta> getActividadById(@PathVariable Long id) {
        return actividadService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/huerta/{huertaId}")
    public List<ActividadHuerta> getActividadesByHuerta(@PathVariable Long huertaId) {
        return actividadService.getByHuerta(huertaId);
    }

    @GetMapping("/cultivo/{cultivoId}")
    public List<ActividadHuerta> getActividadesByCultivo(@PathVariable Long cultivoId) {
        return actividadService.getByCultivo(cultivoId);
    }

    @PostMapping
    public ActividadHuerta createActividad(@RequestBody ActividadHuerta actividad) {
        return actividadService.save(actividad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActividadHuerta> updateActividad(@PathVariable Long id, @RequestBody ActividadHuerta actividad) {
        return actividadService.update(id, actividad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActividad(@PathVariable Long id) {
        boolean eliminado = actividadService.deleteById(id);

        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
