package com.agromatik.cloud.servicio;

import com.agromatik.cloud.dto.SensorDataDTO;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.agromatik.cloud.model.Sensor.EstadoSensor;

@Service
@RequiredArgsConstructor
public class SensorDataGeneratorService {

    private final LecturaSensorService lecturaService;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();

    /**
     * Genera datos aleatorios cada 7 segundos SOLO para sensores ACTIVOS.
     */
    @Scheduled(fixedDelay = 7000)
    public void generateAndSendReadings() {

        //Llamamos al nuevo método del repositorio
        List<Sensor> sensoresActivos = sensorRepository.findByEstado(EstadoSensor.ACTIVO);

        if (sensoresActivos.isEmpty()) {
            System.err.println("ADVERTENCIA: No se pueden generar lecturas. No hay sensores ACTIVOS en la BD.");
            return;
        }

        // Iterar sobre TODOS los sensores ACTIVOS
        for (Sensor sensor : sensoresActivos) {

            String tipo = sensor.getTipoSensor().toString();
            Double valor = generateValueForType(tipo);

            if (valor == null) {
                // Omitir simulación para tipos no definidos (LUZ, VIENTO, etc.)
                continue;
            }

            SensorDataDTO data = SensorDataDTO.builder()
                    .sensorUuid(sensor.getUuid())
                    .valor(valor)
                    .unidad(getUnitForType(tipo))
                    .rawData("{\"simulacion_timestamp\": \"" + LocalDateTime.now() + "\", \"tipo_simulado\": \"" + tipo + "\"}")
                    .build();

            try {
                lecturaService.recibirYProcesarLectura(data);
                System.out.printf("SIMULACIÓN OK: Enviada lectura de %.2f %s para Sensor %s%n", valor, data.getUnidad(), sensor.getNombre());

            } catch (Exception e) {
                System.err.printf("ERROR SIMULACIÓN: Falló el procesamiento de lectura para %s. Mensaje: %s%n", sensor.getNombre(), e.getMessage());
            }
        }
    }

    // --- MÉTODOS AUXILIARES DE SIMULACIÓN ---

    private Double generateValueForType(String tipo) {
        // 15% de probabilidad de alerta
        boolean forzarAlerta = random.nextInt(100) < 15;

        switch (tipo) {
            case "TEMPERATURA":
                if (forzarAlerta) {
                    return 8.0 + random.nextDouble() * 5.0; // 8 a 13°C
                }
                return 25.0 + random.nextGaussian() * 3.0; // Normal

            case "HUMEDAD_SUELO":
                if (forzarAlerta) {
                    return 10.0 + random.nextDouble() * 8.0; // 10 a 18%
                }
                return 40.0 + random.nextGaussian() * 15.0; // Normal

            case "PH":
                if (forzarAlerta) {
                    return 5.0 + random.nextDouble() * 0.5; // Ácido (asumiendo umbral 6.0)
                }
                return 6.5 + random.nextDouble() * 1.5; // Normal

            default:
                // No simular tipos desconocidos
                return null;
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