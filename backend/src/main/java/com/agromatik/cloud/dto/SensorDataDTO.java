package com.agromatik.cloud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Usamos @Data, @Builder, @NoArgsConstructor, y @AllArgsConstructor para simplificar
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDTO {

    //CAMBIO CRÍTICO: De 'sensorUuid' a 'sensorNombre'
    @NotBlank(message = "El nombre del sensor es obligatorio")
    private String sensorNombre;

    // Valor medido por el sensor. Usamos Double para la evaluación de umbrales.
    @NotNull(message = "El valor de la lectura es obligatorio")
    private Double valor;

    // Unidad de la medida (ej: %, C, ppm).
    @NotBlank(message = "La unidad es obligatoria")
    private String unidad;

    // Campo opcional para almacenar el JSON crudo si es necesario.
    private String rawData;
}
