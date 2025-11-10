package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.servicio.CultivoService;
import jakarta.validation.Valid;
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

    /**
     *
     * Devuelve todos los cultivos (si es Admin) o solo los del usuario.
     */
    @GetMapping
    public List<Cultivo> getAllCultivos(){
        return cultivoService.getAllCultivosPorContexto();
    }
    /**
     * Devuelve cultivos de una huerta, si el usuario tiene permiso.
     */
    @GetMapping("/huerta/{huertaId}")
    public List<Cultivo> getAllCultivosByHuerta(@PathVariable Long huertaId){
        return cultivoService.getByHuertaIdPorContexto(huertaId);
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
    public ResponseEntity<Void> deleteCultivo(@PathVariable String uuid) {
        boolean eliminado = cultivoService.deleteByUuid(uuid);

        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204: Éxito, se borró
        } else {
            return ResponseEntity.notFound().build(); // 404: No se encontró
        }
    }

}
