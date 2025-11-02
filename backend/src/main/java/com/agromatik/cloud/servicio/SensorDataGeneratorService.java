package com.agromatik.cloud.servicio;
import com.agromatik.cloud.dto.SensorDataDTO;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SensorDataGeneratorService {

    private final LecturaSensorService lecturaService;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();

    /**
     * Genera datos aleatorios cada 10 segundos para los primeros 3 sensores.
     * Esta simulación imita a la API externa enviando datos.
     */
    @Scheduled(fixedDelay = 60000) // 👈 Se ejecuta cada 7,000 milisegundos (7 segundos)
    public void generateAndSendReadings() {
        // Obtenemos una lista pequeña de sensores para simular la entrada de datos
        List<Sensor> sensoresActivos = sensorRepository.findAll(PageRequest.of(0, 3)).getContent();

        if (sensoresActivos.isEmpty()) {
            System.err.println("ADVERTENCIA: No se pueden generar lecturas. No hay sensores en la BD.");
            return;
        }

        // Iterar sobre los sensores para enviar datos únicos
        for (Sensor sensor : sensoresActivos) {

            String tipo = sensor.getTipoSensor().toString();
            Double valor = generateValueForType(tipo);

            // Creamos el DTO tal como lo espera LecturaSensorController
            SensorDataDTO data = SensorDataDTO.builder()
                    .sensorUuid(sensor.getUuid())
                    .valor(valor)
                    .unidad(getUnitForType(tipo))
                    // ❗ CORRECCIÓN AQUÍ: Usamos un formato JSON válido (clave-valor)
                    .rawData("{\"simulacion_timestamp\": \"" + LocalDateTime.now() + "\", \"tipo_simulado\": \"" + tipo + "\"}")
                    .build();

            try {
                // Llamamos al servicio para guardar y evaluar (el mismo flujo del POST real)
                lecturaService.recibirYProcesarLectura(data);
                System.out.printf("SIMULACIÓN OK: Enviada lectura de %.2f %s para Sensor %s%n", valor, data.getUnidad(), sensor.getNombre());

            } catch (Exception e) {
                System.err.printf("ERROR SIMULACIÓN: Falló el procesamiento de lectura para %s. Mensaje: %s%n", sensor.getNombre(), e.getMessage());
            }
        }
    }

    // --- MÉTODOS AUXILIARES DE SIMULACIÓN ---

    private Double generateValueForType(String tipo) {
        // 1. Reducir la probabilidad de alerta al 7%
        boolean forzarAlerta = random.nextInt(100) < 7;

        switch (tipo) {
            case "TEMPERATURA":
                if (forzarAlerta) {
                    // 7% del tiempo: Generar un valor CRÍTICO/ALTO (ej., debajo de 12°C o arriba de 38°C)

                    // 50% de probabilidad de ser frío (8-12°C) o 50% de ser caliente (38-42°C)
                    if (random.nextBoolean()) {
                        // Alerta Baja (Frío extremo)
                        return 8.0 + random.nextDouble() * 4.0;
                    } else {
                        // Alerta Alta (Calor extremo)
                        return 38.0 + random.nextDouble() * 4.0;
                    }
                }
                // 93% del tiempo: Valor NORMAL (Centrado en 25°C, con desviación de 3°)
                return 25.0 + random.nextGaussian() * 3.0;

            case "HUMEDAD_SUELO":
                // (Umbral de 20% a 75%)
                if (forzarAlerta) {
                    // 7% del tiempo: Generar un valor CRÍTICO/ALTO
                    // Forzamos la sequía (valor muy bajo)
                    return 10.0 + random.nextDouble() * 5.0; // Valores de 10% a 15%
                }
                // 93% del tiempo: Valor NORMAL (Centrado en 50%, más realismo con menor desviación)
                return 50.0 + random.nextGaussian() * 10.0;

            case "PH":
                // Rango seguro del pH (6.5 a 8.0), centrado en 7.0 (neutro)
                if (forzarAlerta) {
                    // Generar PH ácido o alcalino extremo para probar alertas
                    return 5.0 + random.nextDouble() * 0.5; // Ácido extremo: 5.0 - 5.5
                }
                return 7.0 + random.nextGaussian() * 0.5; // Valor normal centrado en 7.0

            default:
                // Mantener otros sensores aleatorios y seguros (Ej: Viento, Luz)
                return 50.0 + random.nextDouble() * 10;
        }
    }

    private String getUnitForType(String tipo) {
        return switch (tipo) {
            case "TEMPERATURA" -> "C";
            case "HUMEDAD_SUELO", "HUMEDAD_AMBIENTAL" -> "%";
            case "PH" -> "pH";
            default -> "unid";
        };
    }
}