package com.portafolio.citas.model.entity;

/**
 * Enumeración que define los diferentes estados posibles por los que puede pasar
 * una cita o reserva a lo largo de su ciclo de vida en el sistema.
 */
public enum AppointmentStatus {
    
    /**
     * La cita ha sido solicitada o creada recientemente y está pendiente de confirmación.
     */
    PENDING,
    
    /**
     * La cita ha sido aprobada, confirmada y agendada exitosamente.
     */
    CONFIRMED,
    
    /**
     * La cita fue cancelada por el cliente o por el administrador del sistema.
     */
    CANCELLED
}
