package com.agromatik.cloud.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/**
 * DTO para transportar estadísticas agregadas (AVG, MIN, MAX)
 * desde consultas JPQL al servicio de reportes.
 */
public class EstadisticasAgregadasDTO {

    private Object fecha; // El día (o la hora) de la agrupación
    private Double promedio;
    private BigDecimal minimo;
    private BigDecimal maximo;
    private Long conteo; // Cuántas lecturas se analizaron en ese día

    /**
     * Constructor especial que usará la consulta JPQL.
     */
    public EstadisticasAgregadasDTO(Object fecha, Double promedio, BigDecimal minimo, BigDecimal maximo, Long conteo) {
        // Convierte la fecha de SQL (si viene así) a LocalDate
        if (fecha instanceof Date) {
            this.fecha = ((Date) fecha).toLocalDate();
        } else {
            this.fecha = fecha;
        }
        this.promedio = promedio;
        this.minimo = minimo;
        this.maximo = maximo;
        this.conteo = conteo;
    }

    // Getters (necesarios para la serialización JSON)
    public Object getFecha() { return fecha; }
    public Double getPromedio() { return promedio; }
    public BigDecimal getMinimo() { return minimo; }
    public BigDecimal getMaximo() { return maximo; }
    public Long getConteo() { return conteo; }
}
