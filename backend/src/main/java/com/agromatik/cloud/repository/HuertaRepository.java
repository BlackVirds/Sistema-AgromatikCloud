package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Huerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuertaRepository extends JpaRepository<Huerta, Long> {
    List<Huerta> findByUsuarioId(Long usuarioId); //listar todas las huertas de un usuario
    Optional<Huerta> findByUuid(String uuid); //buscar una huerta específica por su UUID

    // Busca huertas por el email del usuario autenticado
    List<Huerta> findByUsuarioEmail(String email);

    /**
     * Busca una huerta por su UUID (único) y valida que pertenezca al email del usuario.
     * (Usado para los GET, PUT, y DELETE por UUID seguros).
     */
    Optional<Huerta> findByUuidAndUsuarioEmail(String uuid, String email);
}
