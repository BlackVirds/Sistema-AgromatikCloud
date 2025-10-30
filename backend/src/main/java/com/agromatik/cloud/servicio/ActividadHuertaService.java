package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.ActividadHuerta;
import com.agromatik.cloud.repository.ActividadHuertaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActividadHuertaService {
    private final ActividadHuertaRepository actividadHuertaRepository;

    public ActividadHuertaService(ActividadHuertaRepository actividadHuertaRepository) {
        this.actividadHuertaRepository = actividadHuertaRepository;
    }
    public List<ActividadHuerta> getAll(){
        return actividadHuertaRepository.findAll();
    }

    public List<ActividadHuerta> findByCultivo(Long cultivoId){
        return  actividadHuertaRepository.findByCultivoId(cultivoId);
    }

    public List<ActividadHuerta> findByHuerta(Long huertaId) {
        return actividadHuertaRepository.findByHuertaId(huertaId);
    }

    public Optional<ActividadHuerta> findById(Long id){}
}
