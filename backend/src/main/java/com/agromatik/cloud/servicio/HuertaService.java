package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import static com.agromatik.cloud.util.ValidacionesUtil.validarUbicacion;


import java.util.List;
import java.util.Optional;

@Service
public class HuertaService {
    private final HuertaRepository huertaRepository;
    private final UsuarioRepository usuarioRepository;

    public HuertaService(HuertaRepository huertaRepository, UsuarioRepository usuarioRepository) {

        this.huertaRepository = huertaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Huerta> getAll() {
        return huertaRepository.findAll();
    }

    public Optional<Huerta> getByUuid(String uuid) {
        return huertaRepository.findByUuid(uuid);
    }

    public List<Huerta> getByUsuarioId(Long usuarioId) {
        return huertaRepository.findByUsuarioId(usuarioId);
    }

    public Huerta save(Huerta huerta) {

        Long usuarioId = huerta.getUsuario().getId();

        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para crear una huerta.");
        }

        // 🔹 Validar ubicación geográfica solo si no está vacía
        if (huerta.getUbicacionGeografica() != null && !huerta.getUbicacionGeografica().isBlank()) {
            validarUbicacion(huerta.getUbicacionGeografica());
        }

        // Buscamos al usuario en la BD
        Usuario usuarioExistente = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario con ID: " + usuarioId));

        //Si existe, adjuntamos el objeto Usuario real a la huerta
        huerta.setUsuario(usuarioExistente);

        //guardamos
        return huertaRepository.save(huerta);
    }

    public boolean deleteByUuid(String uuid) {
        return huertaRepository.findByUuid(uuid).map(huerta -> {

            // Si la huerta ya está inactiva, salimos con éxito.
            if (huerta.getActiva() == null || !huerta.getActiva()) {
                return true;
            }

            // 1. Aplicamos el Soft Delete
            huerta.setActiva(false);

            // 2. Guardamos el cambio
            huertaRepository.save(huerta);

            return true;
        }).orElse(false);
    }

    public Optional<Huerta> updateByUuid(String uuid, Huerta updatedHuerta) {
        return huertaRepository.findByUuid(uuid).map(existing -> {

            if (updatedHuerta.getNombre() != null) {
                existing.setNombre(updatedHuerta.getNombre());
            }
            if (updatedHuerta.getDescripcion() != null) {
                existing.setDescripcion(updatedHuerta.getDescripcion());
            }
            if (updatedHuerta.getUbicacionGeografica() != null && !updatedHuerta.getUbicacionGeografica().isBlank()) {
                // 🔹 Valida solo si tiene valor real
                validarUbicacion(updatedHuerta.getUbicacionGeografica());
                existing.setUbicacionGeografica(updatedHuerta.getUbicacionGeografica());
            } else if (updatedHuerta.getUbicacionGeografica() != null && updatedHuerta.getUbicacionGeografica().isBlank()) {
                // 🔹 Si explícitamente mandan una cadena vacía, limpiamos el campo
                existing.setUbicacionGeografica(null);
            }
            if (updatedHuerta.getDireccion() != null) {
                existing.setDireccion(updatedHuerta.getDireccion());
            }
            if (updatedHuerta.getMunicipio() != null) {
                existing.setMunicipio(updatedHuerta.getMunicipio());
            }
            if (updatedHuerta.getEstado() != null) {
                existing.setEstado(updatedHuerta.getEstado());
            }
            if (updatedHuerta.getPais() != null) {
                existing.setPais(updatedHuerta.getPais());
            }
            if (updatedHuerta.getTamañoHectareas() != null) {
                existing.setTamañoHectareas(updatedHuerta.getTamañoHectareas());
            }
            if (updatedHuerta.getTipoSuelo() != null) {
                existing.setTipoSuelo(updatedHuerta.getTipoSuelo());
            }
            if (updatedHuerta.getAltitudMetros() != null) {
                existing.setAltitudMetros(updatedHuerta.getAltitudMetros());
            }
            if (updatedHuerta.getActiva() != null) {
                existing.setActiva(updatedHuerta.getActiva());
            }

            return huertaRepository.save(existing);
        });
    }

}
