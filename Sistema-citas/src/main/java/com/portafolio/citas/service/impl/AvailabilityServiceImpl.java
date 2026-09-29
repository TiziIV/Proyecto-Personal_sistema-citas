package com.portafolio.citas.service.impl;

import com.portafolio.citas.dto.AvailabilityRequestDTO;
import com.portafolio.citas.dto.AvailabilityResponseDTO;
import com.portafolio.citas.model.entity.Appointment;
import com.portafolio.citas.model.entity.AppointmentStatus;
import com.portafolio.citas.model.entity.Availability;
import com.portafolio.citas.model.entity.User;
import com.portafolio.citas.repository.AppointmentRepository;
import com.portafolio.citas.repository.AvailabilityRepository;
import com.portafolio.citas.repository.UserRepository;
import com.portafolio.citas.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la capa de servicio para disponibilidad horaria y cálculo dinámico de slots.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public AvailabilityResponseDTO saveOrUpdateAvailability(AvailabilityRequestDTO dto, String userEmail) {
        if (dto.getStartTime().isAfter(dto.getEndTime()) || dto.getStartTime().equals(dto.getEndTime())) {
            throw new IllegalArgumentException("La hora de inicio debe ser estrictamente anterior a la hora de fin.");
        }

        User admin = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado: " + userEmail));

        Availability availability = availabilityRepository.findByUserAndDayOfWeek(admin, dto.getDayOfWeek())
                .orElse(Availability.builder().user(admin).dayOfWeek(dto.getDayOfWeek()).build());

        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setSlotDurationMinutes(dto.getSlotDurationMinutes());

        Availability saved = availabilityRepository.save(availability);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponseDTO> getAllAvailabilities() {
        return availabilityRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcula dinámicamente los horarios disponibles (slots) para una fecha específica.
     * 
     * ¿Cómo se restan los turnos ocupados a los teóricos?
     * 1. Generación Teórica: A partir de la configuración de disponibilidad del día de la semana correspondiente 
     *    (ej. LUNES de 08:00 a 17:00 con duración de 30 min), generamos todos los bloques horarios posibles (slots teóricos).
     * 2. Consulta de Ocupación: Consultamos todas las citas existentes en la base de datos para esa fecha exacta 
     *    cuyo estado NO sea CANCELLED (es decir, PENDING o CONFIRMED).
     * 3. Filtrado (Resta): Cruzamos ambas listas. Si un slot teórico coincide con la hora exacta de una cita ya reservada, 
     *    se excluye de la lista resultante.
     * 4. Filtrado por Hora Actual: Si la fecha solicitada es el día de hoy (LocalDate.now()), se descartan también 
     *    aquellos horarios que ya hayan transcurrido respecto a la hora actual (LocalTime.now()).
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableSlots(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        List<Availability> availabilities = availabilityRepository.findByDayOfWeek(dayOfWeek);
        if (availabilities.isEmpty()) {
            return Collections.emptyList();
        }

        Availability availability = availabilities.get(0);

        List<LocalTime> theoreticalSlots = new ArrayList<>();
        LocalTime current = availability.getStartTime();
        LocalTime end = availability.getEndTime();
        int durationMinutes = availability.getSlotDurationMinutes() != null ? availability.getSlotDurationMinutes() : 30;

        while (current.plusMinutes(durationMinutes).isBefore(end) || current.plusMinutes(durationMinutes).equals(end)) {
            theoreticalSlots.add(current);
            current = current.plusMinutes(durationMinutes);
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Appointment> bookedAppointments = appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay);

        List<LocalTime> bookedTimes = bookedAppointments.stream()
                .filter(apt -> apt.getStatus() != AppointmentStatus.CANCELLED)
                .map(apt -> apt.getAppointmentDateTime().toLocalTime())
                .collect(Collectors.toList());

        LocalTime nowTime = LocalTime.now();
        boolean isToday = date.isEqual(LocalDate.now());

        return theoreticalSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .filter(slot -> !isToday || slot.isAfter(nowTime))
                .collect(Collectors.toList());
    }

    private AvailabilityResponseDTO mapToResponseDTO(Availability availability) {
        return AvailabilityResponseDTO.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .slotDurationMinutes(availability.getSlotDurationMinutes())
                .userId(availability.getUser() != null ? availability.getUser().getId() : null)
                .build();
    }
}
