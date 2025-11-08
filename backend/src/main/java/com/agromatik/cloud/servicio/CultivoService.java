package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.repository.CultivoRepository;
import com.agromatik.cloud.repository.HuertaRepository;
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

    public CultivoService(CultivoRepository cultivoRepository, HuertaRepository huertaRepository) {
        this.cultivoRepository = cultivoRepository;
        this.huertaRepository = huertaRepository;
    }

    public List<Cultivo> getAll(){
        return cultivoRepository.findAll();
    }
    /**
     * ✅ MÉTODO SEGURO (CONSCIENTE DE ROL)
     * Obtiene todos los cultivos.
     * - Si es ADMIN, devuelve TODOS.
     * - Si es usuario, devuelve SOLO LOS SUYOS (filtrando por email).
     */
    public List<Cultivo> getAllCultivosPorContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (esAdmin) {
            return cultivoRepository.findAll();
        } else {
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            // Usamos tu convención PascalCase
            return cultivoRepository.findByHuertaUsuarioEmail(emailUsuario);
        }
    }

    /**
     * (CONSCIENTE DE ROL)
     * Obtiene los cultivos de UNA huerta, pero solo si el usuario es ADMIN
     * o si esa huerta le pertenece.
     */
    public List<Cultivo> getByHuertaIdPorContexto(Long huertaId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean esAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (esAdmin) {
            // Admin puede ver los cultivos de cualquier huerta
            return cultivoRepository.findByHuertaId(huertaId);
        } else {
            // Usuario normal filtra por huerta Y por su propio email
            String emailUsuario = ((UserDetails) authentication.getPrincipal()).getUsername();
            return cultivoRepository.findByHuertaIdAndHuertaUsuarioEmail(huertaId, emailUsuario);
        }
    }
    public Optional<Cultivo> getByUuid(String uuid){
        return cultivoRepository.findByUuid(uuid);
    }

    public List<Cultivo> getByHuerta(Long huertaId){
        return cultivoRepository.findByHuertaId(huertaId);
    }

    public Optional<Cultivo> updateByUuid(String uuid, Cultivo updated) {
        return cultivoRepository.findByUuid(uuid).map(existing -> {

            if (updated.getTipoCultivo() != null) {
                existing.setTipoCultivo(updated.getTipoCultivo());
            }
            if (updated.getFechaCosechaReal() != null) {
                existing.setFechaCosechaReal(updated.getFechaCosechaReal());
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
            if (updated.getVariedad() != null) {
                existing.setVariedad(updated.getVariedad());
            }
            if (updated.getFechaSiembra() != null) {
                existing.setFechaSiembra(updated.getFechaSiembra());
            }
            if (updated.getFechaCosechaEstimada() != null) {
                existing.setFechaCosechaEstimada(updated.getFechaCosechaEstimada());
            }
            if (updated.getEstado() != null) {
                existing.setEstado(updated.getEstado());
            }
            if (updated.getHuerta() != null && updated.getHuerta().getId() != null) {
                huertaRepository.findById(updated.getHuerta().getId()).ifPresent(existing::setHuerta);
            }
            return cultivoRepository.save(existing);
        });
    }

    public Cultivo save(Cultivo cultivo){

        Long huertaId = cultivo.getHuerta().getId();
        if (huertaId == null) {
            throw new IllegalArgumentException("El ID de la huerta es obligatorio para crear un cultivo.");
        }

        Huerta huertaExistente = huertaRepository.findById(huertaId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la huerta con ID: " + huertaId));

        cultivo.setHuerta(huertaExistente);
        return cultivoRepository.save(cultivo);
    }
    public boolean deleteByUuid(String uuid) {
        return cultivoRepository.findByUuid(uuid).map(cultivo -> {

            // Opcional: Impedir que se borre si ya está cosechado/cancelado
            if (cultivo.getEstado() == Cultivo.EstadoCultivo.COSECHADO || cultivo.getEstado() == Cultivo.EstadoCultivo.CANCELADO) {
                // Si ya terminó el ciclo, no hay nada que hacer, se considera "eliminado" lógicamente.
                return true;
            }

            // Aplicamos el Soft Delete: Marcamos el estado como CANCELADO
            cultivo.setEstado(Cultivo.EstadoCultivo.CANCELADO);

            // Guardamos el cambio (el registro se mantiene)
            cultivoRepository.save(cultivo);

            return true;
        }).orElse(false); // Si no se encuentra el UUID, devuelve false (404 Not Found)
    }
}
