package com.portafolio.citas.repository;

import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
}
