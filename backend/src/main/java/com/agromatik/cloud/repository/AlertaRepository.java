package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Alerta.
 * Incluye métodos personalizados para el estado de lectura de las alertas.
 */
@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Método para paginar todas las alertas según su estado de lectura.
    Page<Alerta> findByLeida(boolean leida, Pageable pageable);

    /**
     * Marca una alerta específica como leída.
     * @param id ID de la alerta a actualizar.
     */
    @Modifying // Indica a Spring que esta consulta modifica datos
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.id = :id")
    void marcarComoLeida(Long id);

    /**
     * Marca todas las alertas no leídas como leídas.
     */
    @Modifying
    @Query("UPDATE Alerta a SET a.leida = true WHERE a.leida = false")
    void marcarTodasComoLeidas();
}