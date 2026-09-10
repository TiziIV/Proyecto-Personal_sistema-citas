package com.portafolio.citas.repository;

import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio de Spring Data JPA para la entidad Appointment.
 * 
 * ¿Por qué extendemos de JpaRepository<Appointment, Long>?
 * Al extender de esta interfaz, Spring Data JPA nos provee automáticamente de todas las operaciones
 * CRUD estándar (guardar, buscar por ID, listar todos, eliminar, contar, paginación, ordenamiento, etc.)
 * sin necesidad de escribir consultas SQL manuales ni implementar métodos repetitivos de acceso a datos.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Consulta derivada (Derived Query) generada automáticamente por Spring Data JPA 
     * a partir de la nomenclatura de su nombre de método.
     * 
     * ¿Qué hace?
     * Verifica si existe (retorna true o false) alguna cita en la base de datos que coincida 
     * con la fecha y hora proporcionada (`appointmentDateTime`), pero cuyo estado sea DIFERENTE (`AndStatusNot`) 
     * al estado especificado por parámetro (`status`).
     * 
     * ¿Por qué ayuda a evitar solapamientos de turnos?
     * Es una herramienta clave para la validación de disponibilidad: cuando un cliente intenta agendar 
     * una cita en un horario determinado, podemos invocar este método pasando por ejemplo `AppointmentStatus.CANCELLED`.
     * De este modo, el sistema detectará si ya hay un turno activo ocupando esa hora (ya sea PENDING o CONFIRMED),
     * mientras ignora correctamente las citas que fueron canceladas (liberando así ese horario para nuevas reservas).
     * 
     * @param appointmentDateTime Fecha y hora de la cita a verificar.
     * @param status Estado que se desea excluir de la validación (ej. CANCELLED).
     * @return true si ya existe otra cita activa en ese horario, false si está libre.
     */
    boolean existsByAppointmentDateTimeAndStatusNot(LocalDateTime appointmentDateTime, AppointmentStatus status);
}
