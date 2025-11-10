package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findByUuid(String uuid);
    List<Sensor> findByHuertaId(Long huertaId);

    //Busca todos los sensores (en todas las huertas) de un usuario por su email.
    List<Sensor> findByHuertaUsuarioEmail(String email);

    //Busca sensores de UNA huerta específica, validando que pertenezca al email del usuario.
    List<Sensor> findByHuertaIdAndHuertaUsuarioEmail(Long huertaId, String email);

    // Para asegurar GET/PUT/DELETE por UUID
    Optional<Sensor> findByUuidAndHuertaUsuarioEmail(String uuid, String email);

    List<Sensor> findByEstado(Sensor.EstadoSensor estado);
}
