package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.ActividadHuerta;
import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.model.Usuario;
import com.agromatik.cloud.repository.ActividadHuertaRepository;
import com.agromatik.cloud.repository.CultivoRepository;
import com.agromatik.cloud.repository.HuertaRepository;
import com.agromatik.cloud.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActividadHuertaService {
    private final ActividadHuertaRepository actividadHuertaRepository;
    private final HuertaRepository huertaRepository;
    private final CultivoRepository cultivoRepository;
    private final UsuarioRepository usuarioRepository;

    public ActividadHuertaService(ActividadHuertaRepository actividadHuertaRepository,
                                  HuertaRepository huertaRepository,
                                  CultivoRepository cultivoRepository,
                                  UsuarioRepository usuarioRepository) {
        this.actividadHuertaRepository = actividadHuertaRepository;
        this.huertaRepository = huertaRepository;
        this.cultivoRepository = cultivoRepository;
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
     * Devuelve todas las actividades (Admin) o solo las del usuario.
     */
    public List<ActividadHuerta> getAll(){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return actividadHuertaRepository.findAll();
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return actividadHuertaRepository.findByHuertaUsuarioEmail(emailUsuario);
        }
    }

    /**
     * Filtra por ID de Cultivo Y por usuario.
     */
    public List<ActividadHuerta> getByCultivo(Long cultivoId){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return actividadHuertaRepository.findByCultivoId(cultivoId);
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return actividadHuertaRepository.findByCultivoIdAndHuertaUsuarioEmail(cultivoId, emailUsuario);
        }
    }

    /**
     * Filtra por ID de Huerta Y por usuario.
     */
    public List<ActividadHuerta> getByHuerta(Long huertaId) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return actividadHuertaRepository.findByHuertaId(huertaId);
        } else {
            String emailUsuario = getEmailUsuario(auth);
            return actividadHuertaRepository.findByHuertaIdAndHuertaUsuarioEmail(huertaId, emailUsuario);
        }
    }

    /**
     * Busca una actividad por ID, solo si eres Admin o si te pertenece.
     */
    public Optional<ActividadHuerta> getById(Long id){
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return actividadHuertaRepository.findById(id);
        } else {
            String email = getEmailUsuario(auth);
            return actividadHuertaRepository.findByIdAndHuertaUsuarioEmail(id, email);
        }
    }
    /**
     * ✅ AHORA ES SEGURO
     * Valida que la huerta (del JSON) pertenezca al usuario (del token).
     */
    @Transactional
    public ActividadHuerta save(ActividadHuerta actividad) {
        Authentication auth = getAuthentication();

        // 1. Validar Huerta (Obligatoria y debe pertenecer al usuario)
        if (actividad.getHuerta() == null || actividad.getHuerta().getId() == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear una actividad.");
        }
        Long huertaId = actividad.getHuerta().getId();
        Huerta huerta = validarPertenenciaHuerta(auth, huertaId);
        actividad.setHuerta(huerta);

        // 2. Validar Cultivo (Opcional, pero si existe, debe pertenecer a la misma huerta)
        if (actividad.getCultivo() != null && actividad.getCultivo().getId() != null) {
            Cultivo cultivo = validarPertenenciaCultivo(auth, actividad.getCultivo().getId(), huertaId);
            actividad.setCultivo(cultivo);
        } else {
            actividad.setCultivo(null);
        }

        // 3. Validar Usuario Responsable (Opcional, solo valida que exista)
        if (actividad.getUsuarioResponsable() != null && actividad.getUsuarioResponsable().getId() != null) {
            Usuario usuario = usuarioRepository.findById(actividad.getUsuarioResponsable().getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario responsable con ID: " + actividad.getUsuarioResponsable().getId()));
            actividad.setUsuarioResponsable(usuario);
        } else {
            actividad.setUsuarioResponsable(null);
        }

        return actividadHuertaRepository.save(actividad);
    }

    @Transactional
    public Optional<ActividadHuerta> update(Long id, ActividadHuerta updatedActividad) {
        // Usa getById (que ya es seguro) para encontrar la actividad y validar pertenencia
        return this.getById(id).map(existing -> {
            Authentication auth = getAuthentication(); // Obtener auth para validaciones de FK

            // 2. Lógica de actualización parcial
            if (updatedActividad.getTipoActividad() != null) {
                existing.setTipoActividad(updatedActividad.getTipoActividad());
            }
            if (updatedActividad.getDescripcion() != null) {
                existing.setDescripcion(updatedActividad.getDescripcion());
            }
            if (updatedActividad.getFechaActividad() != null) {
                existing.setFechaActividad(updatedActividad.getFechaActividad());
            }
            if (updatedActividad.getFechaProgramada() != null) {
                existing.setFechaProgramada(updatedActividad.getFechaProgramada());
            }
            if (updatedActividad.getCompletada() != null) {
                existing.setCompletada(updatedActividad.getCompletada());
            }
            if (updatedActividad.getRecursosUsados() != null) {
                existing.setRecursosUsados(updatedActividad.getRecursosUsados());
            }
            if (updatedActividad.getNotas() != null) {
                existing.setNotas(updatedActividad.getNotas());
            }

            //Validar Re-asignación de Huerta (si se intenta cambiar)
            if (updatedActividad.getHuerta() != null && updatedActividad.getHuerta().getId() != null &&
                    !updatedActividad.getHuerta().getId().equals(existing.getHuerta().getId())) {

                Huerta nuevaHuerta = validarPertenenciaHuerta(auth, updatedActividad.getHuerta().getId());
                existing.setHuerta(nuevaHuerta);
            }

            //Validar Re-asignación de Cultivo (si se intenta cambiar)
            if (updatedActividad.getCultivo() != null && updatedActividad.getCultivo().getId() != null) {
                // Usamos el ID de la huerta (ya sea la 'existing' o la 'nuevaHuerta')
                Long huertaIdActual = existing.getHuerta().getId();
                Cultivo nuevoCultivo = validarPertenenciaCultivo(auth, updatedActividad.getCultivo().getId(), huertaIdActual);
                existing.setCultivo(nuevoCultivo);
            } else if (updatedActividad.getCultivo() != null) {
                existing.setCultivo(null); // Permitir desasignar cultivo
            }

            //Validar Re-asignación de Usuario Responsable (solo valida que exista)
            if (updatedActividad.getUsuarioResponsable() != null && updatedActividad.getUsuarioResponsable().getId() != null) {
                Usuario usuario = usuarioRepository.findById(updatedActividad.getUsuarioResponsable().getId())
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario responsable con ID: " + updatedActividad.getUsuarioResponsable().getId()));
                existing.setUsuarioResponsable(usuario);
            } else if (updatedActividad.getUsuarioResponsable() != null) {
                existing.setUsuarioResponsable(null);
            }

            return actividadHuertaRepository.save(existing);
        });
    }
    /**
     * Completa (Soft Delete) una actividad, solo si eres Admin o si te pertenece.
     */
    @Transactional
    public boolean deleteById(Long id) {
        // Usa getById (que ya es seguro) para encontrar la actividad
        return this.getById(id).map(actividad -> {
            if (actividad.getCompletada() != null && actividad.getCompletada()) {
                return true;
            }
            actividad.setCompletada(true);
            if (actividad.getFechaActividad() == null) {
                actividad.setFechaActividad(LocalDateTime.now());
            }
            actividadHuertaRepository.save(actividad);
            return true;
        }).orElse(false);
    }
    /**
     * Método auxiliar privado para validar pertenencia del cultivo en 'save'
     */
    private Cultivo validarPertenenciaCultivo(Authentication auth, Long cultivoId, Long huertaId) {
        Cultivo cultivo = cultivoRepository.findById(cultivoId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el cultivo con ID: " + cultivoId));

        // Validar que el cultivo pertenezca a la huerta (que ya fue validada)
        if (!cultivo.getHuerta().getId().equals(huertaId)) {
            throw new IllegalArgumentException("Conflicto de datos: El cultivo " + cultivoId + " no pertenece a la huerta " + huertaId + ".");
        }

        // Si no es Admin, hacemos una doble comprobación (redundante pero segura)
        if (!esAdmin(auth)) {
            String email = getEmailUsuario(auth);
            if (!cultivo.getHuerta().getUsuario().getEmail().equals(email)) {
                throw new SecurityException("Acceso denegado. El cultivo " + cultivoId + " no le pertenece.");
            }
        }
        return cultivo;
    }

    // --- MÉTODOS AUXILIARES DE VALIDACIÓN (Internos del Servicio) ---

    /**
     * Método auxiliar privado para validar pertenencia de la huerta en 'save'
     */
    private Huerta validarPertenenciaHuerta(Authentication auth, Long huertaId) {
        if (esAdmin(auth)) {
            return huertaRepository.findById(huertaId)
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + huertaId));
        } else {
            String email = getEmailUsuario(auth);
            // Asumiendo que HuertaRepository tiene findByIdAndUsuarioEmail
            return huertaRepository.findByUsuarioEmail(email).stream()
                    .filter(h -> h.getId().equals(huertaId))
                    .findFirst()
                    .orElseThrow(() -> new SecurityException("Acceso denegado. La huerta " + huertaId + " no le pertenece."));
        }
    }
}
