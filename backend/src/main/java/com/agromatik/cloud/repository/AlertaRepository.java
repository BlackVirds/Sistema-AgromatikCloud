package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Alerta;
import com.agromatik.cloud.dto.ReporteFrecuenciaAlertasDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Alerta.
 * Incluye métodos personalizados para el estado de lectura de las alertas.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Método para paginar todas las alertas según su estado de lectura.
    Page<Alerta> findByLeida(boolean leida, Pageable pageable);

    // --- MÉTODOS NUEVOS PARA FILTRADO DE USUARIO (SEGURIDAD) ---

    //(Seguro) Obtener alertas paginadas, validando pertenencia (por email)
    Page<Alerta> findByUsuarioEmail(String email, Pageable pageable);

    //(Seguro) Obtener alertas por 'leida' Y 'email'
    Page<Alerta> findByLeidaAndUsuarioEmail(boolean leida, String email, Pageable pageable);

    //(Seguro) Obtener una alerta por ID Y email (para getById)
    Optional<Alerta> findByIdAndUsuarioEmail(Long id, String email);


    /**
     * Marca una alerta específica como leída.
     * @param id ID de la alerta a actualizar.
     */
    @Modifying // Indica a Spring que esta consulta modifica datos
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.id = :id")
    void marcarComoLeida(Long id);
    // (Seguro) Marcar como leída solo si te pertenece
    @Modifying
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.id = :id AND a.usuario.email = :email")
    void marcarComoLeidaSiPertenece(Long id, String email);
    /**
     * Marca todas las alertas no leídas como leídas.
     */
    @Modifying
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.leida = false")
    void marcarTodasComoLeidas();

    //(Seguro) Marcar todas como leídas solo si te pertenecen
    @Modifying
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.leida = false AND a.usuario.email = :email")
    void marcarTodasComoLeidasSiPertenecen(String email);

    // MÉTODOS DE REPORTE DE FRECUENCIA (AMBAS OPCIONES) ---

    //**
     //* Opción A (General): Cuenta y agrupa por TODAS las huertas del usuario.
     //*/
    @Query("SELECT new com.agromatik.cloud.dto.ReporteFrecuenciaAlertasDTO(" + // 👈 CORREGIDO
            "a.huerta.id, a.huerta.nombre, a.parametro, COUNT(a.id)) " +
            "FROM Alerta a " +
            "WHERE a.usuario.email = :email " +
            "AND a.fechaCreacion BETWEEN :inicio AND :fin " +
            "GROUP BY a.huerta.id, a.huerta.nombre, a.parametro " +
            "ORDER BY a.huerta.id, COUNT(a.id) DESC")
    List<ReporteFrecuenciaAlertasDTO> getConteoAgrupadoGeneral(
            @Param("email") String email,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    /**
     * Opción B (Específico): Cuenta y agrupa, pero solo para UNA huerta.
     */
    @Query("SELECT new com.agromatik.cloud.dto.ReporteFrecuenciaAlertasDTO(" +
            "a.huerta.id, a.huerta.nombre, a.parametro, COUNT(a.id)) " +
            "FROM Alerta a " +
            "WHERE a.usuario.email = :email " +
            "AND a.huerta.id = :huertaId " +
            "AND a.fechaCreacion BETWEEN :inicio AND :fin " +
            "GROUP BY a.huerta.id, a.huerta.nombre, a.parametro " +
            "ORDER BY COUNT(a.id) DESC")
    List<ReporteFrecuenciaAlertasDTO> getConteoAgrupadoPorHuerta(
            @Param("email") String email,
            @Param("huertaId") Long huertaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}