package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Huerta;
import com.agromatik.cloud.repository.HuertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HuertaService {
    private final HuertaRepository huertaRepository;

    public HuertaService(HuertaRepository huertaRepository) {
        this.huertaRepository = huertaRepository;
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
        return huertaRepository.save(huerta);
    }

    public boolean deleteByUuid(String uuid) {
        return huertaRepository.findByUuid(uuid).map(huerta -> {
            huertaRepository.delete(huerta);
            return true;
        }).orElse(false);
    }

    public Optional<Huerta> updateByUuid(String uuid, Huerta updatedHuerta) {
        return huertaRepository.findByUuid(uuid).map(existing -> {
            existing.setNombre(updatedHuerta.getNombre());
            existing.setDescripcion(updatedHuerta.getDescripcion());
            existing.setDireccion(updatedHuerta.getDireccion());
            existing.setMunicipio(updatedHuerta.getMunicipio());
            existing.setEstado(updatedHuerta.getEstado());
            existing.setPais(updatedHuerta.getPais());
            existing.setTamañoHectareas(updatedHuerta.getTamañoHectareas());
            existing.setTipoSuelo(updatedHuerta.getTipoSuelo());
            existing.setAltitudMetros(updatedHuerta.getAltitudMetros());
            existing.setActiva(updatedHuerta.getActiva());
            return huertaRepository.save(existing);
        });
    }
}
