package com.agromatik.cloud.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
//Define la información general de tu API
@OpenAPIDefinition(
        info = @Info(
                title = "Agromatik Cloud API",
                version = "v1.0",
                description = "Backend API para la gestión de huertas, sensores y análisis de datos agrícolas."
        ),
        // 2. Le dice a Swagger que todos los endpoints (excepto los públicos) requieren el token
        security = @SecurityRequirement(name = "bearerAuth")
)
//Define CÓMO funciona tu seguridad JWT (Bearer Token)
@SecurityScheme(
        name = "bearerAuth", // Nombre de la configuración de seguridad
        description = "Autenticación JWT (Bearer Token). Ingresa 'Bearer' [espacio] y luego tu token.",
        scheme = "bearer", // Esquema (bearer)
        type = SecuritySchemeType.HTTP, // Tipo de seguridad
        bearerFormat = "JWT", // Formato
        in = SecuritySchemeIn.HEADER // Dónde va el token (en el Header)
)
public class OpenApiConfig {
    // Esta clase solo necesita las anotaciones, no requiere métodos.
}