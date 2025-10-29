package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
    Optional<Cultivo> findByUuid(String uuid);
    List<Cultivo> findByHuertaId(Long huertaId);
}
