package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.DynamicInsert;
import java.time.LocalDate;
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

    //Añadir unique = true y length = 255
    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sensor", nullable = false)
    private TipoSensor tipoSensor;

    @Column(length = 100)
    private String modelo;

    @Column(length = 100)
    private String fabricante;

    // Cambiado de Point a String
    @Column(name = "ubicacion_geografica", length = 100)
    private String ubicacionGeografica;

    @Column(name = "fecha_instalacion")
    private LocalDate fechaInstalacion;

    @Column(name = "ultimo_mantenimiento")
    private LocalDate ultimoMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoSensor estado = EstadoSensor.ACTIVO;

    @Column(name = "bateria_nivel")
    private Integer bateriaNivel;

    @Column(columnDefinition = "json")
    private String configuraciones;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="huerta_id", nullable=false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull(message = "La huerta es obligatoria para crear un sensor")
    private Huerta huerta;

    public enum TipoSensor {
        TEMPERATURA,
        HUMEDAD_SUELO,
        HUMEDAD_AMBIENTAL,
        PH,
        LUZ,
        VIENTO,
        LLUVIA
    }

    public enum EstadoSensor {
        ACTIVO,
        INACTIVO,
        MANTENIMIENTO,
        FALLA
    }
}
