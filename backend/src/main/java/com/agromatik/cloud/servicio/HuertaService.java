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

    // --- Métodos Auxiliares de Seguridad ---

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private String getEmailUsuario(Authentication authentication) {
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }


    /**
     * Devuelve todas las huertas (si es Admin) o solo las del usuario.
     */
    public List<Huerta> getHuertasPorContexto() {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return huertaRepository.findAll(); // ADMIN ve TODO
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return huertaRepository.findByUsuarioEmail(emailUsuario);
        }
    }

    /**
     * Busca una huerta por UUID, solo si eres Admin o si te pertenece.
     */
    public Optional<Huerta> getByUuid(String uuid) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return huertaRepository.findByUuid(uuid); // Admin busca por UUID
        } else {
            String email = getEmailUsuario(auth);
            return huertaRepository.findByUuidAndUsuarioEmail(uuid, email);
        }
    }

    /**
     * Obtiene las huertas de un usuario específico (validando permisos).
     */
    public List<Huerta> getByUsuarioId(Long usuarioId) {
        Authentication auth = getAuthentication();
        String emailUsuario = getEmailUsuario(auth);

        if (esAdmin(auth)) {
            return huertaRepository.findByUsuarioId(usuarioId);
        }

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

        if (usuarioAutenticado.getId().equals(usuarioId)) {
            return huertaRepository.findByUsuarioId(usuarioId);
        } else {
            throw new SecurityException("Acceso denegado. No tiene permiso para ver los recursos de este usuario.");
        }
    }

    /**
     * El 'save' ignora el ID de usuario del JSON y usa el ID del token.
     */
    public Huerta save(Huerta huerta) {

        //OBTENER EL USUARIO DEL TOKEN (IGNORA EL JSON)
        Authentication auth = getAuthentication();
        String emailUsuario = getEmailUsuario(auth);

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado. No se puede crear la huerta."));

        //ASIGNAR EL USUARIO DEL TOKEN (Máxima Seguridad)
        huerta.setUsuario(usuarioAutenticado);

        // 3. Validar ubicación (tu lógica existente)
        if (huerta.getUbicacionGeografica() != null && !huerta.getUbicacionGeografica().isBlank()) {
            validarUbicacion(huerta.getUbicacionGeografica());
        }

        return huertaRepository.save(huerta);
    }

    /**
     * Desactiva (Soft Delete) una huerta, solo si te pertenece (o eres Admin).
     */
    public boolean deleteByUuid(String uuid) {
        // Usa getByUuid (que ya es seguro y filtra por rol) para encontrar la huerta.
        // Si no la encuentra O no le pertenece, 'map' no se ejecuta y devuelve false.
        return this.getByUuid(uuid).map(huerta -> {
            if (huerta.getActiva() == null || !huerta.getActiva()) {
                return true;
            }
            huerta.setActiva(false);
            huertaRepository.save(huerta);
            return true;
        }).orElse(false);
    }

    /**
     * Actualiza una huerta, solo si te pertenece (o eres Admin).
     */
    public Optional<Huerta> updateByUuid(String uuid, Huerta updatedHuerta) {
        // Usa getByUuid (que ya es seguro) para encontrar la huerta.
        // Si no la encuentra O no le pertenece, 'map' no se ejecuta.
        return this.getByUuid(uuid).map(existing -> {

            if (updatedHuerta.getNombre() != null) {
                existing.setNombre(updatedHuerta.getNombre());
            }
            if (updatedHuerta.getDescripcion() != null) {
                existing.setDescripcion(updatedHuerta.getDescripcion());
            }
            if (updatedHuerta.getUbicacionGeografica() != null && !updatedHuerta.getUbicacionGeografica().isBlank()) {
                validarUbicacion(updatedHuerta.getUbicacionGeografica());
                existing.setUbicacionGeografica(updatedHuerta.getUbicacionGeografica());
            } else if (updatedHuerta.getUbicacionGeografica() != null && updatedHuerta.getUbicacionGeografica().isBlank()) {
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
