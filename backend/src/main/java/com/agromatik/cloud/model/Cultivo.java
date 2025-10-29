package com.agromatik.cloud.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="cultivos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cultivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length=36)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false, length = 100)
    private String nombre;

    private String tipo;
    private String variedad;

    @Column(name="fecha_siembra")
    private LocalDate fechaSiembra;

    @Column(name = "fecha_cosecha_estimada")
    private LocalDate fechaCosechaEstimada;

    @Column(name = "superficie_hectareas", precision = 10, scale = 2)
    private BigDecimal superficieHectareas;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCultivo estado = EstadoCultivo.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huerta_id", nullable = false)
    private Huerta huerta;

    @Column(name="fecha_creacion")
    private LocalDate fechaCreacion = LocalDate.now();

    private Boolean activo=true;

    public enum EstadoCultivo {
        ACTIVO, FINALIZADO, EN_ESPERA
    }
}
