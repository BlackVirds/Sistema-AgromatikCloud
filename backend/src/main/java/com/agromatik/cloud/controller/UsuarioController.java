package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.servicio.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    @GetMapping
    public List<Usuario> getAllUsuarios(){
        return usuarioService.getAll();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Usuario> getUsuarioByUuid(@PathVariable String uuid) {
        return usuarioService.getByUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Usuario createUsuario(@RequestBody Usuario usuario){
        return usuarioService.save(usuario);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable String uuid, @RequestBody Usuario datosActualizados) {
        return usuarioService.update(uuid, datosActualizados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUsuarioByUuid(@PathVariable String uuid) {
        boolean eliminado = usuarioService.deleteByUuid(uuid);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
