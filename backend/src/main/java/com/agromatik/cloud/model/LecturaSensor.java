package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecturas_sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class LecturaSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación OBLIGATORIA con Sensor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    @NotNull(message = "El sensor es obligatorio")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "huerta",
            "usuario",
            "configuraciones",
            "fechaInstalacion",
            "fabricante",
            "modelo",
            "ultimoMantenimiento",
            "uuid"
    })
    private Sensor sensor;

    // Usamos BigDecimal para garantizar la precisión del valor numérico.
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal valor;

    @Column(nullable = false, length = 20)
    private String unidad;

    @Column(name = "timestamp", columnDefinition = "datetime")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "calidad_dato")
    @Enumerated(EnumType.STRING)
    private CalidadDato calidadDato = CalidadDato.BUENO;

    @Column(name = "raw_data", columnDefinition = "json")
    private String rawData;

    public enum CalidadDato {
        BUENO, REGULAR, POBRE
    }
}
