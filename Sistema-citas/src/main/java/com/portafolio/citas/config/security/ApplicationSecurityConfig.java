package com.portafolio.citas.config.security;

import com.portafolio.citas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración dedicada exclusivamente a los beans de autenticación y persistencia de seguridad.
 * 
 * ¿Por qué extraer estos beans a una clase independiente rompe el ciclo de dependencias?
 * - Anteriormente, SecurityConfig intentaba inyectar el filtro JwtAuthenticationFilter, el cual a su vez 
 *   requería UserDetailsService, y UserDetailsService estaba definido dentro de SecurityConfig, 
 *   creando un bucle cerrado (Ciclo A -> B -> A) que impedía a Spring levantar el contenedor IoC.
 * - Al separar los beans de infraestructura de seguridad (UserDetailsService, PasswordEncoder, AuthenticationProvider) 
 *   en esta clase ApplicationSecurityConfig, desacoplamos la lógica de autenticación de las reglas de enrutamiento HTTP 
 *   y filtros de la cadena SecurityFilterChain, eliminando por completo la dependencia circular.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationSecurityConfig {

    private final UserRepository userRepository;

    /**
     * Define el bean UserDetailsService para buscar usuarios por su email en la base de datos.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + username));
    }

    /**
     * Define el bean PasswordEncoder utilizando BCrypt para el cifrado seguro de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Define el AuthenticationProvider configurado con nuestro UserDetailsService y PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Define el AuthenticationManager necesario para procesar las autenticaciones en el AuthService (Login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
