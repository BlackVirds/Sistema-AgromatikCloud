package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    private Huerta huerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cultivo_id")
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
