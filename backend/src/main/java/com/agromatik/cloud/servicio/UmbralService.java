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

        //Humedad Ambiental (Riesgo de Hongos)
        // (Queremos que esté entre 40% y 80%. Si es > 80% por mucho tiempo, es ALTA/CRITICA)
        umbrales.put("HUMEDAD_AMBIENTAL", new Umbral(40.0, 80.0));

        //Luz (Estrés por alta o baja luz - ej. DLI)
        // (Estos valores dependen mucho del tipo de sensor, ej. 1000-1500 µmol/m²/s)
        umbrales.put("LUZ", new Umbral(400.0, 1500.0));

        //Viento (Daño a estructuras o cultivos)
        // (Solo nos importa el máximo. El mínimo es 0)
        umbrales.put("VIENTO", new Umbral(0.0, 40.0)); // Alerta si es > 40 km/h

        //Lluvia (Riesgo de inundación)
        //
        umbrales.put("LLUVIA", new Umbral(0.0, 25.0)); // Alerta si llueve > 25 mm/hora
    }

    /**
     * Define la severidad de un valor.
     * Devuelve CRITICA, ALTA, MEDIA, o null (si es un valor seguro).
     */
    public Alerta.SeveridadAlerta definirSeveridad(Double valor, Umbral umbral) {
        double rango = umbral.max() - umbral.min();

        // (Márgenes de tolerancia del 30% / 40% que ajustamos antes)
        double margenMedia = rango * 0.3;
        double margenCritico = rango * 0.4;

        // --- NIVEL CRÍTICO ---
        if (valor < umbral.min() - margenCritico || valor > umbral.max() + margenCritico) {
            return SeveridadAlerta.CRITICA;
        }

        // --- NIVEL ALTO ---
        if (valor < umbral.min() || valor > umbral.max()) {
            return SeveridadAlerta.ALTA;
        }

        // --- NIVEL MEDIO ---
        if (valor < umbral.min() + margenMedia || valor > umbral.max() - margenMedia) {
            return SeveridadAlerta.MEDIA;
        }

        // --- SEGURO ---
        return null;
    }

    public record Umbral(Double min, Double max) {}
}