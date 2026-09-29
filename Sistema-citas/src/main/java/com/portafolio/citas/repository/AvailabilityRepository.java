package com.portafolio.citas.repository;

import com.portafolio.citas.model.entity.Availability;
import com.portafolio.citas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad Availability.
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByUser(User user);

    Optional<Availability> findByUserAndDayOfWeek(User user, DayOfWeek dayOfWeek);

    List<Availability> findByDayOfWeek(DayOfWeek dayOfWeek);
}
