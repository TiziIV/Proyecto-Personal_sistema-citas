package com.portafolio.citas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Clase de configuración para habilitar y gestionar la ejecución de tareas asíncronas en Spring Boot.
 * 
 * ¿Por qué es recomendable configurar un ThreadPool dedicado (ThreadPoolTaskExecutor) en lugar de usar el executor por defecto?
 * 1. Control de Recursos: El ejecutor por defecto de Spring (SimpleAsyncTaskExecutor) crea un nuevo hilo para cada tarea,
 *    lo que puede agotar los recursos del sistema (memoria y CPU) bajo alta concurrencia.
 * 2. Predictibilidad y Rendimiento: Un pool de hilos dedicado (`corePoolSize`, `maxPoolSize`, `queueCapacity`) 
 *    limita y recicla hilos de forma eficiente, garantizando que el envío de correos u operaciones secundarias 
 *    no sature el hilo principal de atención de peticiones HTTP de la API REST.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Define un ThreadPool Task Executor dedicado para el envío asíncrono de correos electrónicos.
     * 
     * @return Executor configurado con límites de hilos y cola.
     */
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // Número mínimo de hilos activos en el pool
        executor.setMaxPoolSize(5);       // Número máximo de hilos permitidos
        executor.setQueueCapacity(50);    // Capacidad de la cola de tareas pendientes si todos los hilos están ocupados
        executor.setThreadNamePrefix("EmailAsync-"); // Prefijo identificador para los hilos en los logs
        executor.initialize();
        return executor;
    }
}
