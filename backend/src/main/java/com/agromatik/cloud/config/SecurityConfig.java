package com.agromatik.cloud.config;

import com.agromatik.cloud.servicio.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // ❗ 1. IMPORTAR HttpMethod
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF
                .csrf(csrf -> csrf.disable())

                // REGLAS DE AUTORIZACIÓN
                .authorizeHttpRequests(auth -> auth

                        // 1. Endpoints Públicos (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Solo POST (Registro) es público

                        // 2. Endpoint de Usuario (Borrarse a sí mismo)
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/me").authenticated() // Regla para permitir a los usuarios darde de baja
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/me").authenticated() // Regla para permitir a los usuarios actualizar su informacion

                        // 3. Endpoints de Admin (Gestión de Usuarios)
                        // Todas las demás operaciones en /api/usuarios son SOLO para ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // 4. Endpoints Protegidos (Para usuarios autenticados)
                        .requestMatchers("/api/huertas/**").authenticated()
                        .requestMatchers("/api/sensores/**").authenticated()
                        .requestMatchers("/api/cultivos/**").authenticated()
                        .requestMatchers("/api/lecturas/**").authenticated()
                        .requestMatchers("/api/alertas/**").authenticated()
                        .requestMatchers("/api/actividades-huerta/**").authenticated()

                        // 5. Cualquier otra petición
                        .anyRequest().authenticated()
                )

                // Configurar manejo de sesiones como STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // AÑADIR EL PROVEEDOR AL FILTRO
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}