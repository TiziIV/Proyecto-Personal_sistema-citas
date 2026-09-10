package com.portafolio.citas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la solicitud de creación de una cita.
 * 
 * ¿Por qué usamos un DTO de entrada en lugar de exponer directamente la entidad Appointment?
 * 1. Seguridad: Evita ataques de sobre-asignación (Mass Assignment), donde un usuario malintencionado
 *    podría inyectar campos sensibles o alterados (como un ID generado o un estado pre-confirmado).
 * 2. Desacoplamiento: Aisla la API REST de la estructura interna de la base de datos (Entidad JPA).
 *    Si la base de datos cambia, el contrato de la API puede mantenerse intacto.
 * 3. Validación: Permite aplicar anotaciones de validación específicas para la entrada HTTP 
 *    (como @NotNull, @Email, @Future) sin contaminar el modelo de persistencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    /**
     * Nombre completo del cliente que solicita la cita.
     */
    private String clientName;

    /**
     * Correo electrónico de contacto del cliente.
     */
    private String clientEmail;

    /**
     * Fecha y hora propuesta para la cita.
     */
    private LocalDateTime appointmentDateTime;

    /**
     * Notas o comentarios adicionales aportados por el cliente (opcional).
     */
    private String notes;
}
