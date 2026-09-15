package com.portafolio.citas.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para SpringDoc OpenAPI (Swagger) con soporte para autenticación JWT.
 * 
 * ¿Cómo funciona esta configuración en Swagger UI?
 * 1. SecurityScheme ("BearerAuth"): Define un esquema de seguridad HTTP de tipo Bearer con formato JWT.
 * 2. SecurityRequirement: Aplica este esquema de manera global a todos los endpoints de la API.
 * 3. Botón "Authorize": Al arrancar Swagger UI, aparecerá un botón candado "Authorize" en la esquina superior derecha. 
 *    Al introducir el token JWT obtenido en el login, Swagger inyectará automáticamente la cabecera 
 *    `Authorization: Bearer <token>` en todas las peticiones de prueba que realices desde la interfaz web.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "BearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Reservas y Citas API")
                        .version("2.0")
                        .description("API REST profesional desarrollada en Spring Boot 3 y Spring Security 6, " +
                                     "con persistencia en PostgreSQL y autenticación Stateless basada en JWT. " +
                                     "Incluye control de roles (ROLE_CLIENT y ROLE_ADMIN), prevención de solapamientos y manejo global de errores.")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
