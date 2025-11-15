package com.agromatik.cloud.dto;

/**
 * DTO para el reporte de Frecuencia de Alertas.
 * Contiene el tipo de parámetro (ej. "HUMEDAD_SUELO") y cuántas veces ocurrió.
 */
public class ReporteFrecuenciaAlertasDTO {

    // ❗ CAMPOS AÑADIDOS
    private Long huertaId;
    private String huertaNombre;

    private String parametro;
    private Long conteo;

    //**
     //* Constructor especial que usará la consulta JPQL.
     //
    public ReporteFrecuenciaAlertasDTO(Long huertaId, String huertaNombre, String parametro, Long conteo) {
        this.huertaId = huertaId;
        this.huertaNombre = huertaNombre;
        this.parametro = parametro;
        this.conteo = (conteo != null) ? conteo : 0L;
    }

    // Getters
    public Long getHuertaId() { return huertaId; }
    public String getHuertaNombre() { return huertaNombre; }
    public String getParametro() { return parametro; }
    public Long getConteo() { return conteo; }
}