package com.portafolio.citas.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa la tabla "appointments" en la base de datos relacional.
 * Cada instancia de esta clase modela una cita o turno reservado por un cliente.
 */
@Entity
@Table(name = "appointments") // Define explícitamente el nombre de la tabla en la base de datos relacional
@Getter // Genera automáticamente todos los métodos getter para acceder a los atributos
@Setter // Genera automáticamente todos los métodos setter para modificar los atributos
@NoArgsConstructor // Genera un constructor vacío (requisito fundamental para que JPA pueda instanciar la entidad)
@AllArgsConstructor // Genera un constructor con todos los atributos como parámetros (muy útil para pruebas y builders)
@Builder // Implementa el patrón Builder, permitiendo construir objetos de forma fluida y legible (ej: Appointment.builder()...build())
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // strategy = GenerationType.IDENTITY indica que la clave primaria (ID) es autoincremental y gestionada por la base de datos
    private Long id;

    @Column(nullable = false)
    // El nombre del cliente es obligatorio; no se permite registrar una cita sin este dato
    private String clientName;

    @Column(nullable = false)
    // El correo electrónico del cliente es obligatorio para el envío de notificaciones y recordatorios
    private String clientEmail;

    @Column(nullable = false)
    // Fecha y hora exacta programada para la cita (campo obligatorio)
    private LocalDateTime appointmentDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // @Enumerated(EnumType.STRING) le indica a JPA que guarde el enum como su nombre textual ("PENDING", "CONFIRMED", "CANCELLED")
    // en lugar de su valor numérico ordinal (0, 1, 2), lo cual previene errores si cambia el orden de los valores del enum.
    private AppointmentStatus status;

    // Notas adicionales opcionales o motivos específicos de la cita (puede ser nulo)
    private String notes;
}
