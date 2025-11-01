package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "huertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Huerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, length = 255)
    private String nombre;

    private String descripcion;

    // 🔹 Cambiado de Point a String
    @Column(name = "ubicacion_geografica", length = 255)
    private String ubicacionGeografica;

    private String direccion;

    @Column(length = 100)
    private String municipio;

    @Column(length = 100)
    private String estado;

    @Column(nullable = false, length = 50)
    private String pais = "México";

    @Column(name="tamaño_hectareas", precision=10, scale = 2)
    private BigDecimal tamañoHectareas;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_suelo")
    private TipoSuelo tipoSuelo;

    @Column(name="altitud_metros", precision=8, scale = 2)
    private BigDecimal altitudMetros;
    // Relación con Usuario (muchas huertas pueden pertenecer a un usuario)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name="fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    private Boolean activa = true;

    public enum TipoSuelo {
        ARCILLOSO, ARENOSO, LIMOSO, FRANCO, OTROS
    }
}
