package com.agromatik.cloud.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.api-key}")
    private String principalApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscar el Header "X-API-KEY"
        String requestApiKey = request.getHeader("X-API-KEY");

        // 2. Si la clave existe y coincide con la nuestra...
        if (requestApiKey != null && requestApiKey.equals(principalApiKey)) {

            // 3. Creamos una autenticación "Sistema" con permisos de ADMIN
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "API_SENSOR", // El "usuario" será este nombre
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")) // Le damos permisos
            );

            // 4. Lo dejamos pasar
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continúa con los siguientes filtros (si ya autenticamos aquí, el JwtFilter lo ignorará o pasará)
        filterChain.doFilter(request, response);
    }
}
