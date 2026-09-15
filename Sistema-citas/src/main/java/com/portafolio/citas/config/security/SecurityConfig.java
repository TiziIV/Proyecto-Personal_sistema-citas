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

/**
 * Clase de configuración principal de Spring Security 6 para las reglas de filtrado y autorización HTTP.
 * 
 * Tras la refactorización para evitar dependencias circulares, esta clase solo se encarga 
 * de configurar la cadena de filtros (SecurityFilterChain), inyectando el filtro JWT y el AuthenticationProvider 
 * previamente desacoplados.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configura la cadena de filtros de seguridad (SecurityFilterChain) y las reglas de autorización HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
}
