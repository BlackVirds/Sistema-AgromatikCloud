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

                // REGLAS DE AUTORIZACIÓN REORDENADAS (DE MÁS ESPECÍFICO A GENERAL)
                .authorizeHttpRequests(auth -> auth

                        // 1. Endpoints Públicos (Login y Registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Específico

                        // 2. Endpoints de Usuario (Self-Service)
                        .requestMatchers(HttpMethod.DELETE, "/api/usuarios/me").authenticated() // Específico
                        .requestMatchers(HttpMethod.PUT, "/api/usuarios/me").authenticated() // Específico

                        // 3. Endpoints de Admin (Gestión de Usuarios)
                        // (Estas reglas ahora van ANTES de las de 'authenticated()')
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // General para /usuarios/

                        // Endpoints Protegidos (Para usuarios autenticados)
                        .requestMatchers("/api/huertas/**").authenticated()
                        .requestMatchers("/api/sensores/**").authenticated()
                        .requestMatchers("/api/cultivos/**").authenticated()
                        .requestMatchers("/api/lecturas/**").authenticated()
                        .requestMatchers("/api/alertas/**").authenticated()
                        .requestMatchers("/api/actividades-huerta/**").authenticated()

                        // REGLA DE REPORTES (SOLO USUARIOS, NO ADMIN)
                        // Define explícitamente los roles que SÍ pueden ver reportes
                        .requestMatchers("/api/reportes/**").hasAnyRole("AGRICULTOR", "EMPRESA", "COOPERATIVA")
                        // Cualquier otra petición
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