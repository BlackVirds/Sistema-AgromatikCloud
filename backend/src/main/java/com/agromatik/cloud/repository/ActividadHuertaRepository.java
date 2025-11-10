package com.agromatik.cloud.repository;

import com.agromatik.cloud.model.ActividadHuerta;
import org.springframework.data.jpa.repository.JpaRepository;

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

    // ❗ MÉTODO NUEVO: Para asegurar GET/PUT/DELETE por ID
    Optional<ActividadHuerta> findByIdAndHuertaUsuarioEmail(Long id, String email);
}
