package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.repository.CultivoRepository;
import com.agromatik.cloud.repository.HuertaRepository;
import jakarta.persistence.EntityNotFoundException;
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
            cultivoRepository.delete(cultivo);
            return true;
        }).orElse(false);
    }
}
