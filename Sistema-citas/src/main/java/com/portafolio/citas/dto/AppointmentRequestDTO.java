package com.portafolio.citas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la solicitud de creación de una cita.
 * 
 * ¿Por qué clientName y clientEmail ya no son obligatorios en este DTO de entrada tras incorporar autenticación JWT?
 * - Anteriormente, el cliente HTTP tenía que enviar su nombre y correo en cada petición de reserva.
 * - Ahora, gracias a Spring Security y el Token JWT, el servidor identifica de forma inequívoca al usuario autenticado 
 *   en el backend (`authentication.getName()`), evitando suplantaciones de identidad y simplificando el contrato de la API. 
 *   Si el usuario no especifica un nombre o email alternativo en el DTO, el sistema autocompleta automáticamente 
 *   estos datos utilizando la información de su cuenta registrada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    /**
     * Nombre opcional del cliente o paciente para la cita. 
     * Si no se proporciona, el servicio utilizará por defecto el nombre del usuario autenticado.
     */
    private String clientName;

    /**
     * Correo electrónico opcional de contacto. 
     * Si no se proporciona, el servicio utilizará por defecto el email del usuario autenticado.
     */
    private String clientEmail;

    /**
     * Fecha y hora propuesta para la cita.
     * 
     * @NotNull: Valida que la fecha no sea nula.
     * @Future: Valida que la fecha y hora sean estrictamente posteriores al momento actual.
     */
    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @Future(message = "La fecha de la cita debe ser futura")
    private LocalDateTime appointmentDateTime;

    /**
     * Notas o comentarios adicionales aportados por el cliente (opcional).
     */
    private String notes;
}
