package com.agromatik.cloud.dto;

import com.agromatik.cloud.model.ActividadHuerta;

/**
 * DTO para transportar el resumen estadístico de las actividades de la huerta.
 * Usado para gráficas de eficiencia operativa.
 */
public class ReporteActividadesDTO {

    private ActividadHuerta.TipoActividad tipoActividad;
    private Long totalCompletadas;
    private Long totalPendientes;
    private Long totalActividades;

    /**
     * Constructor especial que usará la consulta JPQL.
     */
    public ReporteActividadesDTO(ActividadHuerta.TipoActividad tipoActividad, Long totalCompletadas, Long totalPendientes) {
        this.tipoActividad = tipoActividad;
        this.totalCompletadas = (totalCompletadas != null) ? totalCompletadas : 0L;
        this.totalPendientes = (totalPendientes != null) ? totalPendientes : 0L;
        this.totalActividades = this.totalCompletadas + this.totalPendientes;
    }

    // Getters
    public ActividadHuerta.TipoActividad getTipoActividad() { return tipoActividad; }
    public Long getTotalCompletadas() { return totalCompletadas; }
    public Long getTotalPendientes() { return totalPendientes; }
    public Long getTotalActividades() { return totalActividades; }
}