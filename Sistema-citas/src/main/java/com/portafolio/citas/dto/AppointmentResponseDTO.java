package com.portafolio.citas.dto;

import com.portafolio.citas.model.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la respuesta que se envía al cliente o frontend tras consultar o crear una cita.
 * 
 * ¿Por qué utilizamos este DTO de respuesta?
 * - Define exactamente qué información es seguro y útil mostrar al usuario o consumidor de la API.
 * - Incluye campos generados por el servidor (como el ID autoincremental, el status inicial PENDING y los datos del usuario dueño)
 *   que el cliente no debe enviar en la petición de creación, pero sí necesita conocer como resultado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {

    /**
     * Identificador único de la cita generado por la base de datos.
     */
    private Long id;

    /**
     * Nombre del cliente registrado en la cita.
     */
    private String clientName;

    /**
     * Correo electrónico del cliente.
     */
    private String clientEmail;

    /**
     * Fecha y hora en la que quedó agendada la cita.
     */
    private LocalDateTime appointmentDateTime;

    /**
     * Estado actual de la cita generado/gestionado por el servidor (PENDING, CONFIRMED, CANCELLED).
     */
    private AppointmentStatus status;

    /**
     * Notas adicionales asociadas a la cita.
     */
    private String notes;

    /**
     * ID del usuario propietario de la reserva.
     */
    private Long userId;

    /**
     * Email del usuario propietario de la reserva.
     */
    private String userEmail;
}
