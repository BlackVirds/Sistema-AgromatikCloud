package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "alertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    // --- Relaciones de tu esquema (necesarias para la navegación) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huerta_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cultivos", "sensores"})
    private Huerta huerta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "huerta"})
    private Sensor sensor;

    // --- Campos de Detección ---
    @Column(name = "parametro", length = 50, nullable = false)
    private String parametro; // Ej: HUMEDAD_SUELO o TEMPERATURA

    @Column(name = "valor_actual", nullable = false)
    private Double valorActual;

    @Column(name = "umbral_min")
    private Double umbralMin;

    @Column(name = "umbral_max")
    private Double umbralMax;

    // --- Campos Comunes ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadAlerta severidad;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(columnDefinition = "boolean")
    private Boolean leida = false;

    @Column(name = "fecha_creacion", columnDefinition = "datetime")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum SeveridadAlerta {
        BAJA,
        MEDIA,
        ALTA,
        CRITICA
    }
}
