package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="usuarios")
@Data
@NoArgsConstructor //Genera un constructor vacío: public Usuario() {}. para crear objetos Usuario cuando los saca de la base de datos
@AllArgsConstructor //Genera un constructor que incluye todos los campos: public Usuario(Long id, String uuid, String email, ...)
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false, length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false, length=255)
    private String email;

    @Column(name = "password_hash", nullable = false, length=255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String apellido;
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    @Column(name="suscription_plan")
    private PlanSuscripcion suscriptionPlan = PlanSuscripcion.BASICO;

    private LocalDate fechaRegistro = LocalDate.now();
    private LocalDateTime ultimoLogin;

    private Boolean activo = true;

    @Column(columnDefinition = "json")
    private String configuraciones;

    public enum TipoUsuario{
        AGRICULTOR, COOPERATIVA, EMPRESA, ADMIN
    }

    public enum PlanSuscripcion{
        BASICO, AVANZADO, EMPRESA
    }
}
