package com.agromatik.cloud.repository;

import com.agromatik.cloud.dto.ReporteCorrelacionDTO;
import com.agromatik.cloud.model.LecturaSensor;
import com.agromatik.cloud.dto.EstadisticasAgregadasDTO; // 1. Importar el DTO
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // MÉTODO NUEVO PARA ANÁLISIS ESTADÍSTICO ---

    /**
     * Calcula las estadísticas (AVG, MIN, MAX, COUNT) agrupadas por DÍA para un sensor específico
     * dentro de un rango de fechas. (Versión segura para Usuario).
     */
    @Query("SELECT new com.agromatik.cloud.dto.EstadisticasAgregadasDTO(FUNCTION('DATE', l.timestamp), AVG(l.valor), MIN(l.valor), MAX(l.valor), COUNT(l.id)) " +
            "FROM LecturaSensor l " +
            "WHERE l.sensor.uuid = :uuid AND l.sensor.huerta.usuario.email = :email " +
            "AND l.timestamp BETWEEN :inicio AND :fin " +
            "GROUP BY FUNCTION('DATE', l.timestamp) " +
            "ORDER BY FUNCTION('DATE', l.timestamp) ASC")
    List<EstadisticasAgregadasDTO> getEstadisticasAgregadasPorDia(
            @Param("uuid") String uuid,
            @Param("email") String email,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * (Seguro - Para Usuario) Proyección para el gráfico (solo fecha y valor).
     */
    @Query("SELECT new com.agromatik.cloud.dto.ReporteCorrelacionDTO$PuntoDatos(l.timestamp, CAST(l.valor AS double)) " +
            "FROM LecturaSensor l " +
            "WHERE l.sensor.uuid = :uuid " +
            "AND l.sensor.huerta.usuario.email = :email " +
            "AND l.timestamp BETWEEN :inicio AND :fin " +
            "ORDER BY l.timestamp ASC")
    List<ReporteCorrelacionDTO.PuntoDatos> findPuntosGrafico(
            @Param("uuid") String uuid,
            @Param("email") String email,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
