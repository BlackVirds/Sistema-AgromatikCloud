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

    // Método para clasificar la severidad según la desviación del valor
    public Alerta.SeveridadAlerta definirSeveridad(Double valor, Umbral umbral) {
        double rango = umbral.max() - umbral.min();
        double margen = rango * 0.1;

        if (valor < umbral.min() - margen || valor > umbral.max() + margen) {
            return SeveridadAlerta.CRITICA;
        } else if (valor < umbral.min() || valor > umbral.max()) {
            return SeveridadAlerta.ALTA;
        } else {
            return SeveridadAlerta.MEDIA;
        }
    }

    public record Umbral(Double min, Double max) {}
}