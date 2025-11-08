package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.Cultivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CultivoRepository extends JpaRepository<Cultivo, Long> {
    Optional<Cultivo> findByUuid(String uuid);
    List<Cultivo> findByHuertaId(Long huertaId);

    //  Busca todos los cultivos (en todas las huertas) de un usuario por su email.
    List<Cultivo> findByHuertaUsuarioEmail(String email);

    // Busca cultivos de UNA huerta específica, validando que pertenezca al email del usuario.
    List<Cultivo> findByHuertaIdAndHuertaUsuarioEmail(Long huertaId, String email);

}
