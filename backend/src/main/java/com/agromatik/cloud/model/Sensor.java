package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.DynamicInsert;
import org.locationtech.jts.geom.Point;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable=false, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sensor", nullable = false)
    private TipoSensor tipoSensor;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100)
    private String fabricante;

    @Column(name = "ubicacion_geografica", columnDefinition = "point")
    private Point ubicacionGeografica;

    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;

    @Column(name = "ultimo_mantenimiento")
    private LocalDate ultimoMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSensor estado = EstadoSensor.activo;

    @Column(name = "bateria_nivel") // Nuevo campo
    private Integer bateriaNivel;

    @Column(columnDefinition = "json") // Nuevo campo
    private String configuraciones; // Mapeado a String por simplicidad

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="huerta_id", nullable=false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Huerta huerta;

    public enum TipoSensor {
        temperatura,
        humedad_suelo,
        humedad_ambiental,
        ph,
        luz,
        viento,
        lluvia
    }

    public enum EstadoSensor {
        activo,
        inactivo,
        mantenimiento,
        falla
    }
}
