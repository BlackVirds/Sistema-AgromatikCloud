package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.servicio.CultivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cultivos")
@CrossOrigin(origins="*")
public class CultivoController {

    private final CultivoService cultivoService;

    public CultivoController(CultivoService cultivoService) {
        this.cultivoService = cultivoService;
    }

    @GetMapping
    public List<Cultivo> getAllCultivos(){
        return cultivoService.getAll();
    }

    //Obtener cultivos por huerta
    @GetMapping("/huerta/{huertaId}")
    public List<Cultivo> getAllCultivosByHuerta(@PathVariable Long huertaId){
        return cultivoService.getByHuerta(huertaId);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Cultivo> getCultivoByUuid(@PathVariable String uuid){
        return cultivoService.getByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cultivo createCultivo(@RequestBody Cultivo cultivo){
        return cultivoService.save(cultivo);
    }


    @PutMapping("/{uuid}")
    public ResponseEntity<Cultivo> updateCultivo(@PathVariable String uuid, @RequestBody Cultivo cultivo) {
        return cultivoService.updateByUuid(uuid, cultivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCultivo(@PathVariable String uuid){
        cultivoService.deleteByUuid(uuid);
        return ResponseEntity.noContent().build();
    }



}
