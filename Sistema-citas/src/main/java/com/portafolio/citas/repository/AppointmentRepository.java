package com.portafolio.citas.repository;

import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByAppointmentDateTimeAndStatusNot(LocalDateTime appointmentDateTime, AppointmentStatus status);

    /**
     * Retorna todas las citas asociadas a un usuario específico.
     * 
     * @param user Entidad User propietaria de las citas.
     * @return Lista de citas del usuario.
     */
    List<Appointment> findByUser(User user);

    /**
     * Busca citas cuyos estados estén contenidos en la colección proporcionada y 
     * cuya fecha y hora se encuentren dentro de un rango temporal específico.
     * 
     * Utilizado principalmente por el programador de tareas (Scheduler) para enviar recordatorios a 24 horas.
     * 
     * @param statuses Colección de estados de cita (ej. PENDING, CONFIRMED).
     * @param start Fecha y hora de inicio del rango.
     * @param end Fecha y hora de fin del rango.
     * @return Lista de citas encontradas en la ventana temporal.
     */
    List<Appointment> findByStatusInAndAppointmentDateTimeBetween(
        Collection<AppointmentStatus> statuses,
        LocalDateTime start,
        LocalDateTime end
    );
}
