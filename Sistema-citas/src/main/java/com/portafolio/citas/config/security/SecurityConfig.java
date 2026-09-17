package com.portafolio.citas.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Clase de configuración principal de Spring Security 6 para las reglas de filtrado, autorización HTTP y CORS.
 * 
 * ¿Qué es CORS (Cross-Origin Resource Sharing) y por qué es necesario configurarlo?
 * 1. Definición y el problema de los puertos:
 *    - CORS es un mecanismo de seguridad implementado por los navegadores web (Same-Origin Policy) 
 *      que impide que una aplicación frontend ejecutada en un origen (por ejemplo, http://localhost:5173 de Vite/React) 
 *      realice peticiones HTTP a un servidor backend en otro origen/puerto (por ejemplo, http://localhost:8080).
 *    - Aunque ambos estén en tu máquina local, los navegadores consideran diferentes puertos como orígenes distintos y bloquean las peticiones por defecto.
 * 2. Solución con CorsConfigurationSource:
 *    - Este bean le indica explícitamente a Spring Security qué orígenes, métodos HTTP y cabeceras tienen permiso de comunicarse con la API,
 *      permitiendo una integración segura y fluida con aplicaciones Frontend modernas (React, Vite, Next.js).
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configura la cadena de filtros de seguridad (SecurityFilterChain) y las reglas de autorización HTTP y CORS.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS con la configuración personalizada
            .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF por ser una API REST Stateless basada en JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin sesiones en servidor
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de autenticación y documentación Swagger
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Endpoints protegidos de citas:
                // DELETE en /api/appointments/** exclusivamente para usuarios con rol ADMIN
                .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasRole("ADMIN")
                
                // Cualquier otra petición a /api/appointments/** requiere estar autenticado (ROLE_CLIENT o ROLE_ADMIN)
                .requestMatchers("/api/appointments/**").authenticated()
                
                // Cualquier otra solicitud en la aplicación requiere autenticación
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            // Registrar nuestro filtro JWT antes del filtro estándar de autenticación de usuario y contraseña
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Define el Bean de configuración CORS para permitir peticiones desde aplicaciones Frontend locales (React / Vite).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos (ej. servidores de desarrollo Frontend como Vite 5173 o React 3000)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Cabeceras HTTP permitidas (incluyendo Authorization para enviar el token JWT)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        
        // Permitir envío de credenciales o cookies de autenticación si se requiere
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración CORS a todas las rutas de la API (/**)
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
