package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.LecturaSensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Repositorio para la entidad LecturaSensor.
 * Extiende JpaRepository para obtener las funcionalidades CRUD básicas.
 */
@Repository
public interface LecturaSensorRepository extends JpaRepository<LecturaSensor, Long> {
    // 1. Obtener la última lectura de un sensor (necesario para el estado actual)
    @Query("SELECT l FROM LecturaSensor l WHERE l.sensor.uuid = :uuid ORDER BY l.timestamp DESC")
    List<LecturaSensor> findLatestBySensorUuid(String uuid, Pageable pageable);

    // 2. Obtener el historial paginado (necesario para el gráfico de tiempo)
    Page<LecturaSensor> findBySensorUuidOrderByTimestampDesc(String uuid, Pageable pageable);

    // 3. Obtener lecturas en un rango de fecha (para análisis específico)
    List<LecturaSensor> findBySensorUuidAndTimestampBetween(String uuid, LocalDateTime inicio, LocalDateTime fin);

    // --- MÉTODOS NUEVOS PARA FILTRADO DE USUARIO (SEGURIDAD) ---

    // (Seguro) Obtener la última lectura, validando pertenencia
    @Query("SELECT l FROM LecturaSensor l WHERE l.sensor.uuid = :uuid AND l.sensor.huerta.usuario.email = :email ORDER BY l.timestamp DESC")
    List<LecturaSensor> findLatestBySensorUuidAndUsuarioEmail(String uuid, String email, Pageable pageable);

    // (Seguro) Obtener historial paginado, validando pertenencia
    Page<LecturaSensor> findBySensorUuidAndSensorHuertaUsuarioEmailOrderByTimestampDesc(String uuid, String email, Pageable pageable);

    //(Seguro) Obtener rango de fechas, validando pertenencia
    List<LecturaSensor> findBySensorUuidAndSensorHuertaUsuarioEmailAndTimestampBetween(String uuid, String email, LocalDateTime inicio, LocalDateTime fin);
}
