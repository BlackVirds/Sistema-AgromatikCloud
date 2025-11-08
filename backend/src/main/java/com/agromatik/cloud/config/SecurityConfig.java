package com.agromatik.cloud.config;


import com.agromatik.cloud.servicio.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider; // 1. IMPORTAR
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // 2. IMPORTAR
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 3. IMPORTAR
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

    // 2. SE CREA EL PROVEEDOR DE AUTENTICACIÓN
    // Le dice a Spring Security CÓMO autenticar (usando JpaUserDetailsService y BCrypt)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(jpaUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. SE EXPONE EL AUTHENTICATION MANAGER
    // Spring Boot 3 crea esto automáticamente. Solo necesitas exponerlo como un Bean.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF
                .csrf(csrf -> csrf.disable())

                // Definir reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (login y registro)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios").permitAll() // Permitir registro de usuarios

                        // Proteger el resto de endpoints
                        .requestMatchers("/api/huertas/**").authenticated()
                        .requestMatchers("/api/sensores/**").authenticated()
                        .requestMatchers("/api/cultivos/**").authenticated()
                        .requestMatchers("/api/lecturas/**").authenticated()
                        .requestMatchers("/api/alertas/**").authenticated()
                        .requestMatchers("/api/actividades-huerta/**").authenticated()

                        // (Opcional) Regla de Admin
                        // .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // Descomentar si e ocupa proteger /api/usuarios

                        // Cualquier otra petición debe ser autenticada
                        .anyRequest().authenticated()
                )

                // Configurar manejo de sesiones como STATELESS
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. ✅ AÑADIR EL PROVEEDOR AL FILTRO
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
