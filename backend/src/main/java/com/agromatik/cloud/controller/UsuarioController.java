package com.agromatik.cloud.controller;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.servicio.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * Endpoint para que un usuario autenticado elimine (desactive) SU PROPIA cuenta.
     * Es un "Soft Delete".
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMiCuenta() {
        // Obtener el email del usuario desde el token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();

        // Llamar a un metodo de servicio para borrar por email
        boolean eliminado = usuarioService.deleteByEmail(emailUsuario);

        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * NUEVO ENDPOINT (PUT /me)
     * Permite a un usuario autenticado actualizar sus propios datos (nombre, teléfono, etc.)
     */
    @PutMapping("/me")
    public ResponseEntity<Usuario> updateMiCuenta(@RequestBody Usuario datosActualizados) {
        String emailUsuario = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return usuarioService.updateMiPerfil(emailUsuario, datosActualizados)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
