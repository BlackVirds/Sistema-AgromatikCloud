package com.agromatik.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class PermitAllSecurityConfig {

    // El @Bean de PasswordEncoder sigue siendo necesario para hashear contraseñas
    // Asegúrate de que tu PasswordEncoder siga estando en la clase SecurityConfig original o aquí.

    /**
     * Define el filtro de seguridad para permitir acceso a todas las rutas.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Autorización: Permite cualquier petición (anyRequest()) sin autenticación (permitAll()).
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                // 2. CSRF: Deshabilita la protección CSRF (necesario para API REST que usa POST/PUT/DELETE)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
