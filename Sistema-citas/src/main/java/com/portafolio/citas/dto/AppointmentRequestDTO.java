package com.portafolio.citas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la solicitud de creación de una cita, 
 * equipado con anotaciones de validación para garantizar la integridad y seguridad de los datos de entrada.
 * 
 * ¿Por qué usamos un DTO de entrada en lugar de exponer directamente la entidad Appointment?
 * 1. Seguridad: Evita ataques de sobre-asignación (Mass Assignment), donde un usuario malintencionado
 *    podría inyectar campos sensibles o alterados (como un ID generado o un estado pre-confirmado).
 * 2. Desacoplamiento: Aisla la API REST de la estructura interna de la base de datos (Entidad JPA).
 * 3. Validación: Permite aplicar anotaciones de validación específicas para la entrada HTTP.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequestDTO {

    /**
     * Nombre completo del cliente.
     * 
     * @NotBlank: Valida que el String no sea nulo, que su longitud sea mayor a 0 
     * y que contenga al menos un carácter que no sea espacio en blanco 
     * (a diferencia de @NotNull que permite cadenas vacías "").
     */
    @NotBlank(message = "El nombre del cliente no puede estar vacio")
    private String clientName;

    /**
     * Correo electrónico de contacto.
     * 
     * @NotBlank: Asegura que el email no esté vacío.
     * @Email: Valida mediante expresión regular que la cadena tenga un formato de correo electrónico válido (ej. usuario@dominio.com).
     */
    @NotBlank(message = "El correo electronico no puede estar vacio")
    @Email(message = "Debe proporcionar un email valido")
    private String clientEmail;

    /**
     * Fecha y hora propuesta para la cita.
     * 
     * @NotNull: Valida que el objeto LocalDateTime no sea nulo.
     * @Future: Valida que la fecha y hora proporcionada sea estrictamente posterior al momento actual del servidor,
     * previniendo reservas en el pasado.
     */
    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @Future(message = "La fecha de la cita debe ser futura")
    private LocalDateTime appointmentDateTime;

    /**
     * Notas o comentarios adicionales opcionales (no requiere validación estricta obligatoria).
     */
    private String notes;
}
