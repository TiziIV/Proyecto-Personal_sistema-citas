package com.portafolio.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de arranque de la aplicación Spring Boot.
 * 
 * Anotación @EnableScheduling:
 * - Habilita la ejecución de tareas programadas (Scheduled Tasks) en segundo plano dentro del contenedor de Spring.
 * - Permite utilizar la anotación @Scheduled en componentes para disparar procesos automáticos 
 *   (como recordatorios diarios, limpieza de datos, reportes, etc.).
 */
@SpringBootApplication
@EnableScheduling
public class CitasApplication {
    public static void main(String[] args) {
        SpringApplication.run(CitasApplication.class, args);
    }
}
