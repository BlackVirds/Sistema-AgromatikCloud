package com.agromatik.cloud.controller;

import com.agromatik.cloud.dto.EstadisticasAgregadasDTO;
import com.agromatik.cloud.dto.ReporteActividadesDTO;
import com.agromatik.cloud.dto.ReporteCorrelacionDTO;
import com.agromatik.cloud.servicio.ReporteService;
import lombok.RequiredArgsConstructor;
import com.agromatik.cloud.dto.ReporteFrecuenciaAlertasDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Endpoint para las gráficas del frontend.
     * Calcula las estadísticas diarias (AVG, MIN, MAX) para un sensor en un rango de fechas.
     * Esta ruta está protegida por SecurityConfig solo para roles de usuario (no ADMIN).
     * * Ejemplo de Petición:
     * GET /api/reportes/sensor/{uuid}?inicio=2025-10-01&fin=2025-10-31
     */
    @GetMapping("/sensor/{uuid}")
    public ResponseEntity<List<EstadisticasAgregadasDTO>> getReporteDiarioSensor(
            @PathVariable String uuid,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<EstadisticasAgregadasDTO> estadisticas = reporteService.getReporteDiarioSensor(uuid, inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }
    /**
     * Endpoint para la gráfica de eficiencia de Actividades (Completadas vs Pendientes).
     * * Ejemplo de Petición:
     * GET /api/reportes/actividades?inicio=2025-10-01&fin=2025-10-31
     */
    @GetMapping("/actividades")
    public ResponseEntity<List<ReporteActividadesDTO>> getReporteEficienciaActividades(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<ReporteActividadesDTO> reporte = reporteService.getReporteEficiencia(inicio, fin);
        return ResponseEntity.ok(reporte);
    }

    //ENDPOINT NUEVO PARA FRECUENCIA DE ALERTAS ---

    /**
     * Endpoint para la gráfica de Frecuencia de Alertas
     * Muestra qué parámetro está fallando más.
     * Acepta un 'huertaId' opcional como Query Param
     * GET /api/reportes/frecuencia-alertas?inicio=...&fin=... (General)
     * GET /api/reportes/frecuencia-alertas?huertaId=2&inicio=...&fin=... (Específico)
     */
    @GetMapping("/frecuencia-alertas") // Ruta general
    public ResponseEntity<List<ReporteFrecuenciaAlertasDTO>> getReporteFrecuencia(
            // huertaId' ES OPCIONAL
            @RequestParam(required = false) Long huertaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<ReporteFrecuenciaAlertasDTO> reporte = reporteService.getReporteFrecuenciaAlertas(huertaId, inicio, fin);
        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para análisis de Causa-Efecto (Gráfico de Líneas + Eventos).
     * GET /api/reportes/correlacion?sensorUuid=...&cultivoId=...&inicio=...&fin=...
     */
    @GetMapping("/correlacion")
    public ResponseEntity<ReporteCorrelacionDTO> getReporteCorrelacion(
            @RequestParam String sensorUuid,
            @RequestParam Long cultivoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        ReporteCorrelacionDTO reporte = reporteService.getReporteCorrelacion(sensorUuid, cultivoId, inicio, fin);
        return ResponseEntity.ok(reporte);
    }
}
