package com.agromatik.cloud.dto;

/**
 * DTO para el reporte de Frecuencia de Alertas.
 * Contiene el tipo de parámetro (ej. "HUMEDAD_SUELO") y cuántas veces ocurrió.
 */
public class ReporteFrecuenciaAlertasDTO {

    private String parametro;
    private Long conteo;

    /**
     * Constructor especial que usará la consulta JPQL.
     */
    public ReporteFrecuenciaAlertasDTO(String parametro, Long conteo) {
        this.parametro = parametro;
        this.conteo = (conteo != null) ? conteo : 0L;
    }

    // Getters
    public String getParametro() { return parametro; }
    public Long getConteo() { return conteo; }
}