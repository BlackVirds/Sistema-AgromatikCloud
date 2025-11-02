package com.agromatik.cloud.servicio;
import com.agromatik.cloud.model.*;
import com.agromatik.cloud.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // 👈 NECESARIO
import org.springframework.data.domain.Pageable; // 👈 NECESARIO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional; // 👈 NECESARIO

@Service
@RequiredArgsConstructor
public class AlertaService {
    private final AlertaRepository alertaRepository;
    private final UmbralService umbralService;

    // --- MÉTODOS DE ESCRITURA (INTELIGENCIA) ---

    @Transactional
    public void evaluarAlertaParaLectura(LecturaSensor lectura) {

        // Obtenemos el tipo del sensor (ej: "HUMEDAD_SUELO")
        String tipoSensor = lectura.getSensor().getTipoSensor().toString();
        Double valor = lectura.getValor().doubleValue();
        UmbralService.Umbral umbral = umbralService.getUmbrales().get(tipoSensor);

        if (umbral == null || valor == null) return;

        if (valor < umbral.min() || valor > umbral.max()) {
            // Se dispara Alerta
            Alerta.SeveridadAlerta severidad = umbralService.definirSeveridad(valor, umbral);
            String titulo = String.format("Umbral violado: %s", tipoSensor);

            Alerta alerta = Alerta.builder()
                    .usuario(lectura.getSensor().getHuerta().getUsuario())
                    .huerta(lectura.getSensor().getHuerta())
                    .sensor(lectura.getSensor())
                    .parametro(tipoSensor)
                    .valorActual(valor)
                    .umbralMin(umbral.min())
                    .umbralMax(umbral.max())
                    .severidad(severidad)
                    .titulo(titulo)
                    .descripcion(String.format("Lectura de %.2f %s fuera del rango [%.2f - %.2f]",
                            valor, lectura.getUnidad(), umbral.min(), umbral.max()))
                    .build();

            alertaRepository.save(alerta);
        }
    }

    // --- MÉTODOS CRUD DE CONSULTA (REQUERIDOS POR EL CONTROLLER) ---

    /**
     * Obtiene una página de alertas para listado general (GET /api/alertas).
     */
    public Page<Alerta> obtenerAlertas(Pageable pageable) {
        return alertaRepository.findAll(pageable);
    }

    /**
     * Obtiene una alerta específica por su ID (GET /api/alertas/{id}).
     */
    public Optional<Alerta> obtenerPorId(Long id) {
        return alertaRepository.findById(id);
    }

    /**
     * Marca una alerta como leída (PUT /api/alertas/{id}/leida).
     */
    @Transactional
    public void marcarComoLeida(Long id) {
        alertaRepository.findById(id).ifPresent(alerta -> {
            // Asumiendo que tu entidad Alerta tiene un campo 'leida' y un setter
            alerta.setLeida(true);
            alertaRepository.save(alerta);
        });
    }
}