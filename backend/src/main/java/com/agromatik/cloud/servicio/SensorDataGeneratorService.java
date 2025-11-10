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

//Importar el enum anidado
import com.agromatik.cloud.model.Sensor.EstadoSensor;

@Service
@RequiredArgsConstructor
public class SensorDataGeneratorService {

    private final LecturaSensorService lecturaService;
    private final SensorRepository sensorRepository;
    private final Random random = new Random();

    /**
     * Genera datos aleatorios cada 70 segundos SOLO para sensores ACTIVOS.
     */
    @Scheduled(fixedDelay = 70000) // 70,000 milisegundos = 70 segundos
    public void generateAndSendReadings() {

        // Llama al método del repositorio para buscar solo sensores ACTIVOS
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
                    //CAMBIO CRÍTICO: Enviar el nombre del sensor
                    .sensorNombre(sensor.getNombre())
                    .valor(valor)
                    .unidad(getUnitForType(tipo))
                    .rawData("...")
                    .build();

            try {
                // Llama al servicio para guardar, validar FK y evaluar alertas
                lecturaService.recibirYProcesarLectura(data);
                System.out.printf("SIMULACIÓN OK: Enviada lectura de %.2f %s para Sensor %s%n", valor, data.getUnidad(), sensor.getNombre());

            } catch (Exception e) {
                System.err.printf("ERROR SIMULACIÓN: Falló el procesamiento de lectura para %s. Mensaje: %s%n", sensor.getNombre(), e.getMessage());
            }
        }
    }

    // --- MÉTODOS AUXILIARES DE SIMULACIÓN ---

    private Double generateValueForType(String tipo) {

        // 6% de probabilidad de generar una alerta
        boolean forzarAlerta = random.nextInt(100) < 6;

        switch (tipo) {
            case "TEMPERATURA":
                if (forzarAlerta) {
                    // 7% del tiempo: Generar un valor CRÍTICO (Frío o Calor extremo)
                    if (random.nextBoolean()) {
                        return 8.0 + random.nextDouble() * 4.0; // Frío (8-12°C)
                    } else {
                        return 38.0 + random.nextDouble() * 4.0; // Calor (38-42°C)
                    }
                }
                // 93% del tiempo: Valor NORMAL (Centrado en 25°C)
                return 25.0 + random.nextGaussian() * 3.0;

            case "HUMEDAD_SUELO":
                if (forzarAlerta) {
                    // 7% del tiempo: Valor CRÍTICO (Sequía)
                    return 10.0 + random.nextDouble() * 5.0; // 10% a 15%
                }
                // 93% del tiempo: Valor NORMAL (Centrado en 50%)
                return 50.0 + random.nextGaussian() * 10.0;

            case "PH":
                if (forzarAlerta) {
                    // 7% del tiempo: Valor CRÍTICO (Ácido)
                    return 5.0 + random.nextDouble() * 0.5; // Ácido (5.0 - 5.5)
                }
                // 93% del tiempo: Valor NORMAL
                return 7.0 + random.nextGaussian() * 0.5;

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