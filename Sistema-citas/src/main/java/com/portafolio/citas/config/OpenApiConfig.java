package com.portafolio.citas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para SpringDoc OpenAPI (Swagger).
 * 
 * ¿Qué es OpenAPI y Swagger UI, y por qué es una buena práctica en equipos de desarrollo?
 * 1. Documentación Viva e Interactiva: OpenAPI es una especificación estándar de la industria para describir APIs RESTful.
 *    Swagger UI genera automáticamente una interfaz web interactiva basada en esta especificación, 
 *    permitiendo visualizar los endpoints, sus parámetros, DTOs y probar las peticiones HTTP directamente desde el navegador.
 * 2. Comunicación y Colaboración: Facilita la comunicación entre desarrolladores Backend, Frontend y QA, 
 *    sirviendo como un contrato claro y actualizado de la API sin necesidad de mantener documentos externos obsoletos.
 * 3. Estandarización: Permite generar clientes o contratos de forma automática en múltiples lenguajes.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define el Bean de configuración global para OpenAPI.
     * 
     * @return Instancia configurada de OpenAPI con metadatos del proyecto.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Reservas y Citas API")
                        .version("1.0")
                        .description("API REST desarrollada en Spring Boot para la gestión profesional de citas y reservas, " +
                                     "incluyendo validaciones de negocio, manejo de excepciones y control de disponibilidad horaria. " +
                                     "Diseñado para portafolio profesional.")
                );
    }
}
