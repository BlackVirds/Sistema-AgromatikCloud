package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.servicio.HuertaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/huertas")
@CrossOrigin(origins = "*")

public class HuertaController {
    private final HuertaService huertaService;

    public HuertaController(HuertaService huertaService) {
        this.huertaService = huertaService;
    }

    @GetMapping
    public List<Huerta> getAllHuertas(){
        return huertaService.getAll();
    }
    //Obtener la huerca por UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<Huerta> getHuertaByUuid(@PathVariable String uuid) {
        return huertaService.getByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Obtener las huertas de un usuario especifico
    @GetMapping("/usuarios/{usuarioId}")
    public List<Huerta> getHuertasByUsuario(@PathVariable Long usuarioId) {
        return huertaService.getByUsuarioId(usuarioId);
    }

    @PostMapping
    public Huerta createHuerta(@RequestBody Huerta huerta){
        return huertaService.save(huerta);
    }

    //Actualizar una huerta por UUID
    @PutMapping("/{uuid}")
    public ResponseEntity<Huerta> updateHuerta(@PathVariable String uuid, @RequestBody Huerta huerta){
        return huertaService.updateByUuid(uuid, huerta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteHuerta(@PathVariable String uuid){
        boolean deleted = huertaService.deleteByUuid(uuid);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }



}
