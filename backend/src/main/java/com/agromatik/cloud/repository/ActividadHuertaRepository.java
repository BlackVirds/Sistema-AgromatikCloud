package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.ActividadHuerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadHuertaRepository extends JpaRepository<ActividadHuerta, Long> {
    List<ActividadHuerta> findByHuertaId(Long huertaId);
    List<ActividadHuerta> findByCultivoId(Long cultivoId);
}
