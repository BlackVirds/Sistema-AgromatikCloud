package com.agromatik.cloud.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="cultivos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
@DynamicInsert
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "tipo_cultivo", nullable = false, length = 100)
    private String tipoCultivo;

    @Column(length = 100)
    private String variedad;

    @Column(name="fecha_siembra", nullable = false)
    private LocalDate fechaSiembra;

    @Column(name = "fecha_cosecha_estimada")
    private LocalDate fechaCosechaEstimada;

    @Column(name = "fecha_cosecha_real")
    private LocalDate fechaCosechaReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCultivo estado = EstadoCultivo.ACTIVO;

    @Column(name = "densidad_siembra", precision = 8, scale = 2)
    private BigDecimal densidadSiembra;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_riego")
    private MetodoRiego metodoRiego;

    @Column(columnDefinition = "text")
    private String notas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huerta_id", nullable = false)
    @NotNull(message = "La huerta es obligatoria para crear un cultivo")
    // "Trae la huerta, PERO ignora estos campos pesados/cíclicos"
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "usuario",
            "cultivos",
            "uuid",
            "pais",
            "tamañoHectareas",
            "altitudMetros",
            "fechaCreacion",
            "tipoSuelo",
            "ubicacionGeografica"//Recursión: No traer la lista de cultivos otra vez
    })
    private Huerta huerta;

    public enum EstadoCultivo {
        PLANIFICADO,
        ACTIVO,
        COSECHADO,
        CANCELADO
    }

    public enum MetodoRiego {
        GOTEO,
        ASPERSION,
        INUNDACION,
        OTROS
    }
}
