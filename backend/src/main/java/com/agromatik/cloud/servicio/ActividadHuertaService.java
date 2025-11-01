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
    public List<ActividadHuerta> getAll(){
        return actividadHuertaRepository.findAll();
    }

    public List<ActividadHuerta> getByCultivo(Long cultivoId){
        return  actividadHuertaRepository.findByCultivoId(cultivoId);
    }

    public List<ActividadHuerta> getByHuerta(Long huertaId) {
        return actividadHuertaRepository.findByHuertaId(huertaId);
    }

    public Optional<ActividadHuerta> getById(Long id){
        return actividadHuertaRepository.findById(id);
    }

    public ActividadHuerta save(ActividadHuerta actividad) {

        if (actividad.getHuerta() == null || actividad.getHuerta().getId() == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear una actividad.");
        }
        Huerta huerta = huertaRepository.findById(actividad.getHuerta().getId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + actividad.getHuerta().getId()));
        actividad.setHuerta(huerta);

        if (actividad.getCultivo() != null && actividad.getCultivo().getId() != null) {
            Cultivo cultivo = cultivoRepository.findById(actividad.getCultivo().getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el cultivo con ID: " + actividad.getCultivo().getId()));
            actividad.setCultivo(cultivo);
        } else {
            actividad.setCultivo(null); // Asegura que sea nulo si no se envía un ID válido
        }

        if (actividad.getUsuarioResponsable() != null && actividad.getUsuarioResponsable().getId() != null) {
            Usuario usuario = usuarioRepository.findById(actividad.getUsuarioResponsable().getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró el usuario responsable con ID: " + actividad.getUsuarioResponsable().getId()));
            actividad.setUsuarioResponsable(usuario);
        } else {
            actividad.setUsuarioResponsable(null);
        }

        return actividadHuertaRepository.save(actividad);
    }

    public Optional<ActividadHuerta> update(Long id, ActividadHuerta updatedActividad) {
        return actividadHuertaRepository.findById(id).map(existing -> {

            // Actualizaciones de campos simples
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


            // Validar Huerta (si se intenta cambiar)
            if (updatedActividad.getHuerta() != null && updatedActividad.getHuerta().getId() != null) {
                Huerta huerta = huertaRepository.findById(updatedActividad.getHuerta().getId())
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + updatedActividad.getHuerta().getId()));
                existing.setHuerta(huerta);
            }

            // Validar Cultivo (si se intenta cambiar)
            if (updatedActividad.getCultivo() != null && updatedActividad.getCultivo().getId() != null) {
                Cultivo cultivo = cultivoRepository.findById(updatedActividad.getCultivo().getId())
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró el cultivo con ID: " + updatedActividad.getCultivo().getId()));
                existing.setCultivo(cultivo);
            } else if (updatedActividad.getCultivo() != null) {
                existing.setCultivo(null);
            }

            // Validar Usuario Responsable (si se intenta cambiar)
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
    public boolean deleteById(Long id) {
        return actividadHuertaRepository.findById(id).map(actividad -> {

            // 1. Opcional: Si ya está marcada como completada, no hacemos nada más
            if (actividad.getCompletada() != null && actividad.getCompletada()) {
                return true;
            }

            // 2. Aplicamos el Soft Delete/Finalización Lógica:
            // Marcamos la actividad como COMPLETADA (true).
            actividad.setCompletada(true);

            // 3. Opcional: Si no tenía una fecha de actividad real, puedes registrar la actual
            if (actividad.getFechaActividad() == null) {
                actividad.setFechaActividad(LocalDateTime.now());
            }

            // 4. Guardamos el cambio (el registro se mantiene en la BD)
            actividadHuertaRepository.save(actividad);

            return true;
        }).orElse(false); // Si no se encuentra el ID, devuelve false (404 Not Found)
    }
}
