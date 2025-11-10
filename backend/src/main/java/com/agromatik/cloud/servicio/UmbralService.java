package com.agromatik.cloud.servicio;

import com.agromatik.cloud.model.Alerta;
import com.agromatik.cloud.model.Alerta.SeveridadAlerta;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UmbralService {

    @Getter
    private final Map<String, Umbral> umbrales = new HashMap<>();

    public UmbralService() {
        // Inicializar umbrales para los tipos de sensor
        umbrales.put("HUMEDAD_SUELO", new Umbral(20.0, 75.0));
        umbrales.put("TEMPERATURA", new Umbral(15.0, 35.0));
        umbrales.put("PH", new Umbral(6.0, 7.5));
    }

    /**
     * Define la severidad de un valor.
     * Devuelve CRITICA, ALTA, MEDIA, o null (si es un valor seguro).
     */
    public Alerta.SeveridadAlerta definirSeveridad(Double valor, Umbral umbral) {
        double rango = umbral.max() - umbral.min();

        // 1. Definir márgenes (ej. 10% para MEDIA, 20% para CRITICA)
        double margenMedia = rango * 0.1; // 10% de advertencia
        double margenCritico = rango * 0.2; // 20% de peligro (fuera del rango)

        // --- NIVEL CRÍTICO ---
        // (Muy por debajo o muy por encima del umbral)
        if (valor < umbral.min() - margenCritico || valor > umbral.max() + margenCritico) {
            return SeveridadAlerta.CRITICA;
        }

        // --- NIVEL ALTO ---
        // (Fuera del umbral, pero dentro del margen crítico)
        if (valor < umbral.min() || valor > umbral.max()) {
            return SeveridadAlerta.ALTA;
        }

        // --- NIVEL MEDIO ---
        // (Aún dentro del rango seguro, pero acercándose al límite)
        if (valor < umbral.min() + margenMedia || valor > umbral.max() - margenMedia) {
            return SeveridadAlerta.MEDIA;
        }

        // --- SEGURO ---
        // (El valor está dentro del rango seguro y lejos de los límites)
        return null;
    }

    public record Umbral(Double min, Double max) {}
}