package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.CultivoRepository;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CultivoService {

    private final CultivoRepository cultivoRepository;
    private final HuertaRepository huertaRepository;
    private final UsuarioRepository usuarioRepository;

    public CultivoService(CultivoRepository cultivoRepository, HuertaRepository huertaRepository,  UsuarioRepository usuarioRepository) {
        this.cultivoRepository = cultivoRepository;
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
     * Obtiene todos los cultivos.
     * - Si es ADMIN, devuelve TODOS.
     * - Si es usuario, devuelve SOLO LOS SUYOS (filtrando por email).
     */
    public List<Cultivo> getAllCultivosPorContexto() {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return cultivoRepository.findAll();
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return cultivoRepository.findByHuertaUsuarioEmail(emailUsuario);
        }
    }

    /**
     * (CONSCIENTE DE ROL)
     * Obtiene los cultivos de UNA huerta, pero solo si el usuario es ADMIN
     * o si esa huerta le pertenece.
     */
    public List<Cultivo> getByHuertaIdPorContexto(Long huertaId) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return cultivoRepository.findByHuertaId(huertaId);
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return cultivoRepository.findByHuertaIdAndHuertaUsuarioEmail(huertaId, emailUsuario);
        }
    }
    public Optional<Cultivo> getByUuid(String uuid) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return cultivoRepository.findByUuid(uuid);
        } else {
            String email = getEmailUsuario(auth);
            return cultivoRepository.findByUuidAndHuertaUsuarioEmail(uuid, email);
        }
    }

    /**
     * Actualiza un cultivo, solo si eres Admin o si el cultivo te pertenece.
     * También valida la pertenencia de la nueva huerta si se intenta reasignar.
     */
    public Optional<Cultivo> updateByUuid(String uuid, Cultivo updated) {

        // Llama al GeyByUuid SEGURO (que ya tienes en tu clase).
        // Si no es Admin y el cultivo no le pertenece, 'getByUuid' devuelve Optional.empty()
        // y el '.map()' no se ejecuta.
        return this.getByUuid(uuid).map(existing -> {

            // 2. Lógica de actualización parcial (Setters)
            if (updated.getTipoCultivo() != null) {
                existing.setTipoCultivo(updated.getTipoCultivo());
            }
            if (updated.getVariedad() != null) {
                existing.setVariedad(updated.getVariedad());
            }
            if (updated.getFechaSiembra() != null) {
                existing.setFechaSiembra(updated.getFechaSiembra());
            }
            if (updated.getFechaCosechaEstimada() != null) {
                existing.setFechaCosechaEstimada(updated.getFechaCosechaEstimada());
            }
            if (updated.getFechaCosechaReal() != null) {
                existing.setFechaCosechaReal(updated.getFechaCosechaReal());
            }
            if (updated.getEstado() != null) {
                existing.setEstado(updated.getEstado());
            }
            if (updated.getDensidadSiembra() != null) {
                existing.setDensidadSiembra(updated.getDensidadSiembra());
            }
            if (updated.getMetodoRiego() != null) {
                existing.setMetodoRiego(updated.getMetodoRiego());
            }
            if (updated.getNotas() != null) {
                existing.setNotas(updated.getNotas());
            }

            // Lógica de Re-asignación de Huerta (SEGURA)
            // Si el JSON incluye un objeto 'huerta' con un 'id'
            if (updated.getHuerta() != null && updated.getHuerta().getId() != null) {

                Authentication auth = getAuthentication();
                Long nuevaHuertaId = updated.getHuerta().getId();

                // (Validación de la nueva huerta)
                Huerta nuevaHuerta = huertaRepository.findById(nuevaHuertaId)
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + nuevaHuertaId));

                // (Validación de Pertenencia Manual)
                if (!esAdmin(auth)) {
                    String emailUsuarioToken = getEmailUsuario(auth);
                    // Comparamos el email del token con el email del dueño de la huerta
                    if (!nuevaHuerta.getUsuario().getEmail().equals(emailUsuarioToken)) {
                        throw new SecurityException("Acceso denegado. No tiene permiso para asignar este cultivo a esa huerta.");
                    }
                }
                // Si es Admin O el email coincide, la asignación es segura
                existing.setHuerta(nuevaHuerta);
            }

            return cultivoRepository.save(existing);
        });
    }

    public Cultivo save(Cultivo cultivo){
        Authentication auth = getAuthentication();
        Long huertaId = cultivo.getHuerta().getId();

        if (huertaId == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear un cultivo.");
        }

        // Validar que la huerta exista (Usando el método estándar)
        Huerta huertaExistente = huertaRepository.findById(huertaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + huertaId));

        //Validar Pertenencia (La lógica que faltaba)
        if (!esAdmin(auth)) {
            String emailUsuarioToken = getEmailUsuario(auth);
            // Obtenemos el ID del usuario autenticado
            Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioToken)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));

            // Comparamos el ID del token con el ID del dueño de la huerta
            if (!huertaExistente.getUsuario().getId().equals(usuarioAutenticado.getId())) {
                // El usuario está intentando guardar en la huerta de OTRA PERSONA.
                throw new SecurityException("Acceso denegado. No tiene permiso para crear cultivos en esta huerta.");
            }
        }

        // 3. Si es Admin O el email coincide, la asignación es segura
        cultivo.setHuerta(huertaExistente);
        return cultivoRepository.save(cultivo);
    }
    /**
     * Desactiva un cultivo, solo si eres Admin o si te pertenece.
     */
    public boolean deleteByUuid(String uuid) {
        // Usa getByUuid (que ya es seguro) para encontrar el cultivo
        return this.getByUuid(uuid).map(cultivo -> {
            if (cultivo.getEstado() == Cultivo.EstadoCultivo.COSECHADO || cultivo.getEstado() == Cultivo.EstadoCultivo.CANCELADO) {
                return true;
            }
            cultivo.setEstado(Cultivo.EstadoCultivo.CANCELADO);
            cultivoRepository.save(cultivo);
            return true;
        }).orElse(false);
    }
}
