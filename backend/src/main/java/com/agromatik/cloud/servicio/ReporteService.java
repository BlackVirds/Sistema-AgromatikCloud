package com.agromatik.cloud.servicio;

import com.agromatik.cloud.dto.EstadisticasAgregadasDTO;
import com.agromatik.cloud.dto.ReporteCorrelacionDTO;
import com.agromatik.cloud.repository.*;
import lombok.RequiredArgsConstructor;
import com.agromatik.cloud.dto.ReporteFrecuenciaAlertasDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.agromatik.cloud.dto.ReporteActividadesDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final LecturaSensorRepository lecturaRepository;
    private final ActividadHuertaRepository actividadHuertaRepository;
    private final AlertaRepository alertaRepository;
    private final SensorRepository sensorRepository;
    private final CultivoRepository cultivoRepository;

    // --- Métodos Auxiliares de Seguridad ---

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private String getEmailUsuario(Authentication authentication) {
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    private boolean esAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    /**
     * Genera un reporte de estadísticas diarias (Promedio, Min, Max) para un sensor
     * en un rango de fechas, validando la pertenencia del usuario.
     */
    public List<EstadisticasAgregadasDTO> getReporteDiarioSensor(String sensorUuid, LocalDate inicio, LocalDate fin) {

        Authentication auth = getAuthentication();
        LocalDateTime inicioDT = inicio.atStartOfDay();
        LocalDateTime finDT = fin.atTime(LocalTime.MAX);

        // El servicio de reportes solo sirve al usuario autenticado
        String email = getEmailUsuario(auth);

        // Llama a la consulta segura
        return lecturaRepository.getEstadisticasAgregadasPorDia(sensorUuid, email, inicioDT, finDT);
    }

    //REPORTE DE ACTIVIDADES ---

    /**
     * Genera un reporte de eficiencia (Completadas vs. Pendientes)
     * de las actividades del usuario autenticado en un rango de fechas.
     */
    public List<ReporteActividadesDTO> getReporteEficiencia(LocalDate inicio, LocalDate fin) {

        Authentication auth = getAuthentication();
        String email = getEmailUsuario(auth);

        LocalDateTime inicioDT = inicio.atStartOfDay();
        LocalDateTime finDT = fin.atTime(LocalTime.MAX);

        return actividadHuertaRepository.getReporteEficienciaActividades(email, inicioDT, finDT);
    }

    //MÉTODO NUEVO PARA REPORTE DE FRECUENCIA DE ALERTAS ---

    /**
     * Genera un reporte de frecuencia de alertas.
     * Si huertaId es null, agrupa todas las huertas del usuario.
     * Si huertaId NO es null, filtra solo para esa huerta.
     */
    public List<ReporteFrecuenciaAlertasDTO> getReporteFrecuenciaAlertas(
            Long huertaId, // 👈 AHORA ES OPCIONAL (puede ser null)
            LocalDate inicio,
            LocalDate fin) {

        Authentication auth = getAuthentication();
        String email = getEmailUsuario(auth);
        LocalDateTime inicioDT = inicio.atStartOfDay();
        LocalDateTime finDT = fin.atTime(LocalTime.MAX);

        if (huertaId == null) {
            // Opción A: General (Agrupado por todas sus huertas)
            return alertaRepository.getConteoAgrupadoGeneral(email, inicioDT, finDT);
        } else {
            // Opción B: Específico (Filtrado por una huerta)
            // (La consulta JPQL ya valida la pertenencia (email + huertaId))
            return alertaRepository.getConteoAgrupadoPorHuerta(email, huertaId, inicioDT, finDT);
        }
    }
    //MÉTODO DE REPORTE DE CORRELACIÓN (CORREGIDO)

    /**
     * Reporte de Correlación: Cruza lecturas de un sensor con actividades de un cultivo.
     * Este reporte es SOLO para el usuario autenticado (no Admin).
     */
    public ReporteCorrelacionDTO getReporteCorrelacion(String sensorUuid, Long cultivoId, LocalDate inicio, LocalDate fin) {
        Authentication auth = getAuthentication();
        String email = getEmailUsuario(auth); // Siempre obtenemos el email
        LocalDateTime inicioDT = inicio.atStartOfDay();
        LocalDateTime finDT = fin.atTime(LocalTime.MAX);

        // 1. VALIDACIÓN DE SEGURIDAD (Validar pertenencia)
        boolean sensorEsMio = sensorRepository.findByUuidAndHuertaUsuarioEmail(sensorUuid, email).isPresent();
        boolean cultivoEsMio = cultivoRepository.findById(cultivoId).map(c -> c.getHuerta().getUsuario().getEmail().equals(email)).orElse(false);

        if (!sensorEsMio || !cultivoEsMio) {
            throw new SecurityException("Acceso denegado. El sensor o el cultivo no le pertenecen.");
        }

        // 2. OBTENER DATOS (Llamar a las consultas seguras)

        List<ReporteCorrelacionDTO.PuntoDatos> lecturas =
                lecturaRepository.findPuntosGrafico(sensorUuid, email, inicioDT, finDT);

        List<ReporteCorrelacionDTO.EventoActividad> actividades =
                actividadHuertaRepository.findEventosGrafico(cultivoId, email, inicioDT, finDT);

        // 3. CONSTRUIR REPORTE
        return ReporteCorrelacionDTO.builder()
                .sensorUuid(sensorUuid)
                .cultivoId(cultivoId)
                .historialLecturas(lecturas)
                .actividadesRealizadas(actividades)
                .build();
    }
}
