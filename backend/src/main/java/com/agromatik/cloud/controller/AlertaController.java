package com.agromatik.cloud.controller;
import com.agromatik.cloud.model.Alerta;
import com.agromatik.cloud.servicio.AlertaService; // Usar el servicio que creamos
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/alertas") // Endpoint para consultar alertas
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    /**
     * Obtiene todas las alertas paginadas. Este es el endpoint clave para la prueba.
     * Endpoint: GET /api/alertas?pagina=0&tamano=20
     */
    @GetMapping
    public ResponseEntity<Page<Alerta>> obtenerAlertas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {

        // Asumiendo que el AlertaServicio tiene un método obtenerAlertas(Pageable)
        return ResponseEntity.ok(alertaService.obtenerAlertas(PageRequest.of(pagina, tamano)));
    }

    /**
     * Obtiene una alerta por su ID.
     * Endpoint: GET /api/alertas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Alerta> obtenerAlertaPorId(@PathVariable Long id) {

        // Asumiendo que el AlertaServicio tiene un método obtenerPorId(Long id)
        return alertaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca una alerta como leída.
     * Endpoint: PUT /api/alertas/{id}/leida
     */
    @PutMapping("/{id}/leida")
    public ResponseEntity<Void> marcarAlertaComoLeida(@PathVariable Long id) {

        // Asumiendo que el AlertaServicio tiene un método marcarComoLeida(Long id)
        alertaService.marcarComoLeida(id);
        return ResponseEntity.noContent().build();
    }
}
