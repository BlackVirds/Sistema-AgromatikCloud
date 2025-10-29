package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Cultivo;
import com.agromatik.cloud.repository.CultivoRepository;
import com.agromatik.cloud.repository.HuertaRepository;
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
            // Actualiza solo si el nuevo valor no es null (evita sobrescribir con null)
            if (updated.getNombre() != null) existing.setNombre(updated.getNombre());
            if (updated.getTipo() != null) existing.setTipo(updated.getTipo());
            if (updated.getVariedad() != null) existing.setVariedad(updated.getVariedad());
            if (updated.getFechaSiembra() != null) existing.setFechaSiembra(updated.getFechaSiembra());
            if (updated.getFechaCosechaEstimada() != null) existing.setFechaCosechaEstimada(updated.getFechaCosechaEstimada());
            if (updated.getSuperficieHectareas() != null) existing.setSuperficieHectareas(updated.getSuperficieHectareas());
            if (updated.getEstado() != null) existing.setEstado(updated.getEstado());
            if (updated.getActivo() != null) existing.setActivo(updated.getActivo());

            // Si en el body viene huerta con id, validar que exista y reasignar
            if (updated.getHuerta() != null && updated.getHuerta().getId() != null) {
                huertaRepository.findById(updated.getHuerta().getId()).ifPresent(existing::setHuerta);
            }

            return cultivoRepository.save(existing);
        });
    }

    public Cultivo save(Cultivo cultivo){
        return cultivoRepository.save(cultivo);
    }

    public void deleteByUuid(String uuid){
        cultivoRepository.findByUuid(uuid).ifPresent(cultivoRepository::delete);
    }
}
