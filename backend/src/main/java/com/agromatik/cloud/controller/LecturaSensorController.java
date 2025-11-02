package com.agromatik.cloud.controller;

import com.agromatik.cloud.dto.SensorDataDTO;
import com.agromatik.cloud.model.LecturaSensor;
import com.agromatik.cloud.servicio.LecturaSensorService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lecturas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LecturaSensorController {

    private final LecturaSensorService lecturaService;

    // --- MÉTODOS DE ESCRITURA (POST) ---

    @PostMapping
    public ResponseEntity<LecturaSensor> recibirLectura(@Valid @RequestBody SensorDataDTO data) {

        LecturaSensor nuevaLectura = lecturaService.recibirYProcesarLectura(data);

        // 202 Accepted: El procesamiento de alertas está en curso.
        return ResponseEntity.accepted().body(nuevaLectura);
    }

    // --- MÉTODOS DE LECTURA (GET) PARA EL HISTORIAL ---

    /**
     * Obtiene la última lectura registrada para un sensor específico por su UUID.
     * Endpoint: GET /api/lecturas/sensor/{uuid}/ultima
     */
    @GetMapping("/sensor/{uuid}/ultima")
    public ResponseEntity<LecturaSensor> obtenerUltimaLecturaPorSensor(@PathVariable String uuid) {
        // Llama al servicio para obtener solo el primer elemento (el más reciente)
        return lecturaService.findLatestBySensorUuid(uuid)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene una lista paginada del historial de lecturas de un sensor.
     * Endpoint: GET /api/lecturas/sensor/{uuid}?pagina=0&tamano=100
     */
    @GetMapping("/sensor/{uuid}")
    public ResponseEntity<Page<LecturaSensor>> obtenerHistorialPorSensor(
            @PathVariable String uuid,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "100") int tamano) {

        Page<LecturaSensor> lecturas = lecturaService.findHistoryBySensorUuid(uuid, PageRequest.of(pagina, tamano));
        return ResponseEntity.ok(lecturas);
    }

    /**
     * Obtiene lecturas de un sensor dentro de un rango de fechas.
     * Endpoint: GET /api/lecturas/sensor/{uuid}/rango?inicio=2025-10-01&fin=2025-10-31
     */
    @GetMapping("/sensor/{uuid}/rango")
    public ResponseEntity<List<LecturaSensor>> obtenerLecturasPorRangoDeFecha(
            @PathVariable String uuid,
            // Spring convierte automáticamente el String '2025-10-01' a LocalDate
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin) {

        List<LecturaSensor> lecturas = lecturaService.findRangeBySensorUuid(uuid, inicio, fin);
        return ResponseEntity.ok(lecturas);
    }
}