package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Huerta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuertaRepository extends JpaRepository<Huerta, Long> {
    List<Huerta> findByUsuarioId(Long usuarioId); //listar todas las huertas de un usuario
    Optional<Huerta> findByUuid(String uuid); //buscar una huerta específica por su UUID
}
