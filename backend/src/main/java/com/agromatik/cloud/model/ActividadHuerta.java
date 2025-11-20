package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Table(name="actividades_huerta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@DynamicInsert
public class ActividadHuerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="huerta_id", nullable = false)
    @NotNull(message = "La huerta es obligatoria")
    // Ignoramos todo lo pesado de la huerta
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "usuario",              // Recursión
            "descripcion", "ubicacionGeografica", "direccion",
            "pais", "tamañoHectareas", "tipoSuelo", "altitudMetros",
            "fechaCreacion", "activa"
    })
    private Huerta huerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cultivo_id")
    // Ignoramos lo pesado del cultivo
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "huerta",               // Recursión (ya tenemos la huerta arriba)
            "notas", "fechaSiembra", "fechaCosechaEstimada",
            "fechaCosechaReal", "densidadSiembra", "metodoRiego"
    })
    private Cultivo cultivo;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_actividad", nullable = false)
    private TipoActividad tipoActividad;

    private String descripcion;

    private LocalDateTime fechaActividad;
    private LocalDateTime fechaProgramada;

    private Boolean completada = false;

    @Column(columnDefinition = "json")
    private String recursosUsados;

    private String notas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable")
    // Ignoramos TODO lo que no sea identificación básica
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "passwordHash", "huertas", "configuraciones", "suscriptionPlan",
            "fechaRegistro", "ultimoLogin", "activo", "tipo",
    })
    private Usuario usuarioResponsable;

    public enum TipoActividad {
        RIEGO,
        FERTILIZACION,
        PODA,
        APLICACION_PESTICIDA,
        COSECHA,
        MUESTREO
    }
}
