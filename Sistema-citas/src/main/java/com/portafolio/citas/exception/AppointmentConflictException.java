package com.portafolio.citas.exception;

/**
 * Excepción personalizada de negocio lanzada cuando ocurre un conflicto con las citas
 * (por ejemplo, cuando se intenta reservar en un horario que ya se encuentra ocupado).
 * 
 * ¿Por qué es mejor lanzar una excepción de negocio propia en lugar de un RuntimeException genérico?
 * 1. Legibilidad y Claridad: Expresa claramente la intención del negocio en el código.
 * 2. Manejo de Errores Específico (Clean Architecture / ControllerAdvice): Permite capturar esta excepción
 *    de forma centralizada en un @ControllerAdvice para retornar un código HTTP adecuado (por ejemplo, HTTP 409 Conflict)
 *    con un mensaje claro y amigable para el cliente, en lugar de un genérico HTTP 500 Server Error.
 */
public class AppointmentConflictException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje descriptivo del conflicto ocurrido.
     * 
     * @param message Mensaje explicando la razón del conflicto de negocio.
     */
    public AppointmentConflictException(String message) {
        super(message);
    }
}
