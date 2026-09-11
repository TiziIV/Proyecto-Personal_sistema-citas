package com.portafolio.citas.exception;

/**
 * Excepción personalizada de negocio lanzada cuando se intenta buscar o manipular 
 * un recurso (por ejemplo, una cita) que no existe en la base de datos.
 * 
 * ¿Por qué es importante lanzar una excepción específica en lugar de retornar null o un error genérico?
 * 1. Claridad Semántica: Comunica exactamente que el recurso solicitado no fue hallado.
 * 2. Manejo HTTP Adecuado: Permite interceptar esta excepción globalmente para retornar un 
 *    código de estado HTTP 404 NOT FOUND, el estándar RESTful para recursos inexistentes.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor que recibe un mensaje descriptivo indicando cuál recurso no fue encontrado.
     * 
     * @param message Mensaje explicativo del error.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
