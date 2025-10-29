package com.agromatik.cloud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Sensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable=false, length = 36)
    private String uuid = UUID.randomUUID().toString();

    @Column(nullable=false, length = 100)
    private String tipo;

    private String modelo;

    @Column(columnDefinition = "text")
    private String descripcion;

    private String ubicacion;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDate fechaInstalacion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="huerta_id", nullable=false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Huerta huerta;
}
