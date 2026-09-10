package com.portafolio.citas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta la excepción MethodArgumentNotValidException que se dispara automáticamente
     * cuando falla alguna validación de un DTO anotado con @Valid.
     * 
     * ¿Cómo ayuda esto al desarrollador Frontend?
     * - Extrae cada error de validación por campo (FieldError) y construye un mapa limpio 
     *   donde la clave es el nombre del atributo del formulario (ej. "clientEmail") y el valor
     *   es el mensaje descriptivo en español (ej. "Debe proporcionar un email valido").
     * - De esta forma, el Frontend puede iterar fácilmente sobre este diccionario JSON y pintar 
     *   los mensajes de error directamente debajo de cada input correspondiente en el formulario web o móvil.
     * 
     * @param ex Excepción de validación de argumentos.
     * @return ResponseEntity con un Map de errores por campo y código HTTP 400 BAD REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        
        // Recorremos todos los errores de validación ocurridos en los campos del DTO
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.BAD_REQUEST.value());
        errorBody.put("error", "Validation Failed");
        errorBody.put("messages", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflictException(AppointmentConflictException ex) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.CONFLICT.value());
        errorBody.put("error", "Conflict");
        errorBody.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

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
