package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByUuid(String uuid);
    List<Sensor> findByHuertaId(Long huertaId);
}
