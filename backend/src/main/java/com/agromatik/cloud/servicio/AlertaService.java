package com.agromatik.cloud.servicio;
import com.agromatik.cloud.model.*;
import com.agromatik.cloud.repository.AlertaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // 👈 NECESARIO
import org.springframework.data.domain.Pageable; // 👈 NECESARIO
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    // --- MÉTODOS CRUD DE CONSULTA (AHORA SEGUROS) ---

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

    /**
     *Obtiene una página de alertas (Seguro)
     */
    public Page<Alerta> obtenerAlertas(Pageable pageable) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return alertaRepository.findAll(pageable);
        } else {
            String email = getEmailUsuario(auth);
            return alertaRepository.findByUsuarioEmail(email, pageable);
        }
    }

    /**
     *Obtiene una alerta específica por su ID (Seguro)
     */
    public Optional<Alerta> obtenerPorId(Long id) {
        Authentication auth = getAuthentication();
        if (esAdmin(auth)) {
            return alertaRepository.findById(id);
        } else {
            String email = getEmailUsuario(auth);
            return alertaRepository.findByIdAndUsuarioEmail(id, email);
        }
    }

    /**
     *Marca una alerta como leída (Seguro)
     */
    @Transactional
    public void marcarComoLeida(Long id) {
        Authentication auth = getAuthentication();

        Optional<Alerta> alertaOpt = alertaRepository.findById(id);
        if (alertaOpt.isEmpty()) {
            throw new EntityNotFoundException("Alerta no encontrada");
        }

        Alerta alerta = alertaOpt.get();

        if (esAdmin(auth)) {
            // Admin puede marcarla
            alerta.setLeida(true);
            alertaRepository.save(alerta);
        } else {
            // Usuario normal solo marca si le pertenece
            String email = getEmailUsuario(auth);
            if (alerta.getUsuario().getEmail().equals(email)) {
                alerta.setLeida(true);
                alertaRepository.save(alerta);
            } else {
                throw new SecurityException("Acceso denegado. No tiene permiso para modificar esta alerta.");
            }
        }
    }
}