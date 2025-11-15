package com.agromatik.cloud.repository;

import com.agromatik.cloud.dto.ReporteCorrelacionDTO;
import com.agromatik.cloud.model.ActividadHuerta;
import com.agromatik.cloud.dto.ReporteActividadesDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActividadHuertaRepository extends JpaRepository<ActividadHuerta, Long> {
    List<ActividadHuerta> findByHuertaId(Long huertaId);
    List<ActividadHuerta> findByCultivoId(Long cultivoId);

    // MÉTODOS NUEVOS PARA FILTRADO POR USUARIO AUTENTICADO ---

    //FILTRADO GENERAL: Buscar actividades por el email del dueño de la huerta
    List<ActividadHuerta> findByHuertaUsuarioEmail(String email);

    //FILTRADO DOBLE: Buscar actividades por ID de huerta y por email de usuario (seguridad)
    List<ActividadHuerta> findByHuertaIdAndHuertaUsuarioEmail(Long huertaId, String email);

    //FILTRADO DOBLE: Buscar actividades por ID de cultivo y por email de usuario (seguridad)
    List<ActividadHuerta> findByCultivoIdAndHuertaUsuarioEmail(Long cultivoId, String email);

    //Para asegurar GET/PUT/DELETE por ID
    Optional<ActividadHuerta> findByIdAndHuertaUsuarioEmail(Long id, String email);
    // MÉTODO NUEVO PARA ANÁLISIS DE ACTIVIDADES ---

    /**
     * Calcula el conteo de actividades completadas vs. pendientes para un usuario,
     * agrupadas por tipo de actividad.
     * (Versión Segura para Usuario).
     */
    @Query("SELECT new com.agromatik.cloud.dto.ReporteActividadesDTO(" +
            "a.tipoActividad, " +
            "SUM(CASE WHEN a.completada = true THEN 1 ELSE 0 END), " + // Conteo de completadas
            "SUM(CASE WHEN a.completada = false THEN 1 ELSE 0 END)) " + // Conteo de pendientes
            "FROM ActividadHuerta a " +
            "WHERE a.huerta.usuario.email = :email " +
            "AND a.fechaActividad BETWEEN :inicio AND :fin " + // Filtrar por rango de fechas
            "GROUP BY a.tipoActividad")
    List<ReporteActividadesDTO> getReporteEficienciaActividades(
            @Param("email") String email,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Proyección para los marcadores de eventos en el gráfico.
     * (Versión Segura para Usuario).
     */
    @Query("SELECT new com.agromatik.cloud.dto.ReporteCorrelacionDTO$EventoActividad(a.fechaActividad, a.tipoActividad, a.descripcion) " +
            "FROM ActividadHuerta a " +
            "WHERE a.cultivo.id = :cultivoId " +
            "AND a.huerta.usuario.email = :email " + // Filtro de seguridad
            "AND a.completada = true " +
            "AND a.fechaActividad BETWEEN :inicio AND :fin " +
            "ORDER BY a.fechaActividad ASC")
    List<ReporteCorrelacionDTO.EventoActividad> findEventosGrafico(
            @Param("cultivoId") Long cultivoId,
            @Param("email") String email, // Añadido email para seguridad
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

}
