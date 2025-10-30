package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> getAll(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid);
    }

    public Optional<Usuario> update(String uuid, Usuario datosActualizados) {
        return usuarioRepository.findByUuid(uuid).map(usuarioExistente -> {

            if (datosActualizados.getNombre() != null) {
                usuarioExistente.setNombre(datosActualizados.getNombre());
            }
            if (datosActualizados.getApellido() != null) {
                usuarioExistente.setApellido(datosActualizados.getApellido());
            }
            if (datosActualizados.getTelefono() != null) {
                usuarioExistente.setTelefono(datosActualizados.getTelefono());
            }
            if (datosActualizados.getTipo() != null) {
                usuarioExistente.setTipo(datosActualizados.getTipo());
            }
            if (datosActualizados.getSuscriptionPlan() != null) {
                usuarioExistente.setSuscriptionPlan(datosActualizados.getSuscriptionPlan());
            }
            if (datosActualizados.getActivo() != null) {
                usuarioExistente.setActivo(datosActualizados.getActivo());
            }
            if (datosActualizados.getConfiguraciones() != null) {
                usuarioExistente.setConfiguraciones(datosActualizados.getConfiguraciones());
            }

            return usuarioRepository.save(usuarioExistente);
        });
    }

    public Usuario save(Usuario usuario){
        usuarioRepository.findByEmail(usuario.getEmail()).ifPresent(u -> {
            throw new IllegalStateException("El email " + usuario.getEmail() + " ya está registrado.");
        });
        // Obtenemos la contraseña en texto plano que envió el usuario
        String plainPassword = usuario.getPasswordHash();

        // La 'hasheamos'
        String hashedPassword = passwordEncoder.encode(plainPassword);

        // Reemplazamos el texto plano por el hash
        usuario.setPasswordHash(hashedPassword);

        return usuarioRepository.save(usuario);
    }

    public boolean deleteByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid).map(usuario -> {
            usuarioRepository.delete(usuario);
            return true;
        }).orElse(false);
    }
}
