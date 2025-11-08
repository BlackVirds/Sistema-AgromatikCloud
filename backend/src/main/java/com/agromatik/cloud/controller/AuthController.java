package com.agromatik.cloud.controller;

import com.agromatik.cloud.config.JwtService;
import com.agromatik.cloud.dto.AuthRequestDTO;
import com.agromatik.cloud.dto.AuthResponseDTO;
import com.agromatik.cloud.servicio.JpaUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JpaUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JpaUserDetailsService userDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        // 1. Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        // 2. Si es exitoso, cargar UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

        // 3. Generar el token
        String token = jwtService.generateToken(userDetails);

        // 4. Devolver el token
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}