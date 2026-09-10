package com.portafolio.citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación REST.
 * 
 * ¿Cómo funciona @RestControllerAdvice y por qué es una buena práctica?
 * - @RestControllerAdvice actúa como un interceptor global (AOP) para todos los controladores (@RestController).
 * - Cuando cualquier servicio o controlador lanza una excepción no manejada, este componente la intercepta,
 *   evitando que la aplicación devuelva un genérico y feo error HTTP 500 (Internal Server Error) con stacktrace crudo al frontend.
 * - Permite transformar las excepciones de negocio en respuestas HTTP limpias, estructuradas y con códigos de error semánticos (ej. 409 Conflict, 400 Bad Request).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta la excepción de negocio AppointmentConflictException cuando ocurre un conflicto de horario.
     * 
     * @param ex La excepción capturada.
     * @return ResponseEntity con un cuerpo JSON estructurado y código HTTP 409 CONFLICT.
     */
    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflictException(AppointmentConflictException ex) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.CONFLICT.value());
        errorBody.put("error", "Conflict");
        errorBody.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

    /**
     * Manejador genérico opcional para IllegalArgumentException (ej. validaciones de fechas pasadas o IDs no encontrados).
     * Retorna HTTP 400 BAD_REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Bad Request");
        errorBody.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
}
