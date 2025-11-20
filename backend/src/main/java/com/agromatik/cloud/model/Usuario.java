package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="usuarios")
@Data
@NoArgsConstructor //Genera un constructor vacío: public Usuario() {}. para crear objetos Usuario cuando los saca de la base de datos
@AllArgsConstructor //Genera un constructor que incluye todos los campos: public Usuario(Long id, String uuid, String email, ...)
@Builder
@DynamicInsert
public class Usuario {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable = false, length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false, length=255)
    @Email(message = "El formato del email no es válido")
    private String email;

    @Column(name = "password_hash", nullable = false, length=255)
    @NotBlank(message = "La contraseña es obligatoria")
    @JsonIgnore
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    @Column(name="subscription_plan")
    private PlanSuscripcion suscriptionPlan = PlanSuscripcion.BASICO;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "ultimo_login")
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
