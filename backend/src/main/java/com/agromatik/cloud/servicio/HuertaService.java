package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import static com.agromatik.cloud.util.ValidacionesUtil.validarUbicacion;
// Imports de Seguridad
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


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

    /**
     * Devuelve todas las huertas (si es Admin) o solo las del usuario.
     */
    public List<Huerta> getHuertasPorContexto() {
        // 1. Obtener la identidad del token JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Revisar si el usuario tiene el rol ADMIN
        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        // 3. Lógica de filtrado
        if (esAdmin) {
            return huertaRepository.findAll(); // ADMIN ve TODO
        } else {
            // Usuario normal filtra por su email (que es el username)
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            return huertaRepository.findByUsuarioEmail(emailUsuario);
        }
    }

    public Optional<Huerta> getByUuid(String uuid) {
        return huertaRepository.findByUuid(uuid);
    }

    /**
     * Permite obtener las huertas de un usuario específico,
     * solo si el usuario autenticado es ADMIN o si pide sus propias huertas.
     */
    public List<Huerta> getByUsuarioId(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();

        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));;

        // 1. El Admin puede ver las huertas de CUALQUIER ID
        if (esAdmin) {
            return huertaRepository.findByUsuarioId(usuarioId);
        }

        // 2. Un usuario normal solo puede ver sus propias huertas
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

        if (usuarioAutenticado.getId().equals(usuarioId)) {
            return huertaRepository.findByUsuarioId(usuarioId);
        } else {
            // Acceso denegado si intenta espiar los recursos de otro usuario.
            throw new SecurityException("Acceso denegado. No tiene permiso para ver los recursos de este usuario.");
        }
    }

    public Huerta save(Huerta huerta) {

        Long usuarioId = huerta.getUsuario().getId();

        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para crear una huerta.");
        }

        // Validar ubicación geográfica solo si no está vacía
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
