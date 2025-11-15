package com.agromatik.cloud.dto;

import com.agromatik.cloud.model.ActividadHuerta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteCorrelacionDTO {

    private String sensorUuid;
    private Long cultivoId;

    // La línea del gráfico (Datos del sensor)
    private List<PuntoDatos> historialLecturas;

    // Los puntos/marcadores en el gráfico (Eventos)
    private List<EventoActividad> actividadesRealizadas;

    // --- Clases internas para estructurar el JSON ---

    @Data
    @AllArgsConstructor
    public static class PuntoDatos {
        private LocalDateTime fecha;
        private Double valor;
    }

    @Data
    @AllArgsConstructor
    public static class EventoActividad {
        private LocalDateTime fecha;
        private ActividadHuerta.TipoActividad tipo;
        private String descripcion;
    }
}