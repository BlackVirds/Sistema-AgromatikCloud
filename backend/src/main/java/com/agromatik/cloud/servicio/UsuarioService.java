package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioReporsitory){
        this.usuarioRepository=usuarioReporsitory;
    }

    public List<Usuario> getAll(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid);
    }

    public Optional<Usuario> update(String uuid, Usuario datosActualizados) {
        return usuarioRepository.findByUuid(uuid).map(usuarioExistente -> {
            // Solo actualizamos campos modificables
            usuarioExistente.setNombre(datosActualizados.getNombre());
            usuarioExistente.setApellido(datosActualizados.getApellido());
            usuarioExistente.setTelefono(datosActualizados.getTelefono());
            usuarioExistente.setTipo(datosActualizados.getTipo());
            usuarioExistente.setSuscriptionPlan(datosActualizados.getSuscriptionPlan());
            usuarioExistente.setActivo(datosActualizados.getActivo());
            usuarioExistente.setConfiguraciones(datosActualizados.getConfiguraciones());
            return usuarioRepository.save(usuarioExistente);
        });
    }

    public Usuario save(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public boolean deleteByUuid(String uuid) {
        return usuarioRepository.findByUuid(uuid).map(usuario -> {
            usuarioRepository.delete(usuario);
            return true;
        }).orElse(false);
    }
}
