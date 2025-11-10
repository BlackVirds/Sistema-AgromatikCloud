package com.agromatik.cloud.servicio;

import com.agromatik.cloud.dto.SensorDataDTO;
import com.agromatik.cloud.model.LecturaSensor;
import com.agromatik.cloud.model.Sensor;
import com.agromatik.cloud.repository.LecturaSensorRepository;
import com.agromatik.cloud.repository.SensorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LecturaSensorService {

    private final LecturaSensorRepository lecturaRepository;
    private final SensorRepository sensorRepository;
    private final AlertaService alertaService;

    // --- MÉTODOS CRUD/PROCESAMIENTO (Existentes) ---

    @Transactional
    public LecturaSensor recibirYProcesarLectura(SensorDataDTO dto) {

        // CAMBIO CRÍTICO: Buscar el sensor por NOMBRE (el ID externo)
        Sensor sensor = sensorRepository.findByNombre(dto.getSensorNombre())
                .orElseThrow(() -> new EntityNotFoundException("Sensor no encontrado con Nombre: " + dto.getSensorNombre()));

        // 2. MAPEO: Crear la entidad LecturaSensor
        BigDecimal valorDecimal = BigDecimal.valueOf(dto.getValor());

        LecturaSensor lectura = LecturaSensor.builder()
                .sensor(sensor)
                .valor(valorDecimal)
                .unidad(dto.getUnidad())
                .timestamp(LocalDateTime.now())
                .rawData(dto.getRawData())
                .build();

        // 3. PERSISTENCIA: Guardar el dato crudo
        LecturaSensor savedLectura = lecturaRepository.save(lectura);

        // 4. INTELIGENCIA: Disparar el motor de evaluación de alertas
        alertaService.evaluarAlertaParaLectura(savedLectura);

        return savedLectura;
    }
    // --- MÉTODOS DE CONSULTA DE HISTORIAL (AHORA SEGUROS) ---

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private String getEmailUsuario(Authentication authentication) {
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }
    // --- MÉTODOS DE CONSULTA DE HISTORIAL (Faltantes) ---

    /**
     * Obtiene la ÚLTIMA lectura (Seguro)
     */
    public Optional<LecturaSensor> findLatestBySensorUuid(String uuid) {
        Authentication auth = getAuthentication();
        Pageable limit = PageRequest.of(0, 1);

        if (esAdmin(auth)) {
            return lecturaRepository.findLatestBySensorUuid(uuid, limit).stream().findFirst();
        } else {
            String email = getEmailUsuario(auth);
            return lecturaRepository.findLatestBySensorUuidAndUsuarioEmail(uuid, email, limit).stream().findFirst();
        }
    }

    /**
     *  Obtiene el HISTORIAL paginado (Seguro)
     */
    public Page<LecturaSensor> findHistoryBySensorUuid(String uuid, Pageable pageable) {
        Authentication auth = getAuthentication();

        if (esAdmin(auth)) {
            return lecturaRepository.findBySensorUuidOrderByTimestampDesc(uuid, pageable);
        } else {
            String email = getEmailUsuario(auth);
            return lecturaRepository.findBySensorUuidAndSensorHuertaUsuarioEmailOrderByTimestampDesc(uuid, email, pageable);
        }
    }

    /**
     * Obtiene lecturas en un RANGO de fecha (Seguro)
     */
    public List<LecturaSensor> findRangeBySensorUuid(String uuid, LocalDate inicio, LocalDate fin) {
        Authentication auth = getAuthentication();
        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(LocalTime.MAX);

        if (esAdmin(auth)) {
            return lecturaRepository.findBySensorUuidAndTimestampBetween(uuid, inicioDateTime, finDateTime);
        } else {
            String email = getEmailUsuario(auth);
            return lecturaRepository.findBySensorUuidAndSensorHuertaUsuarioEmailAndTimestampBetween(uuid, email, inicioDateTime, finDateTime);
        }
    }
}