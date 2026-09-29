package com.portafolio.citas.service;

import com.portafolio.citas.dto.AvailabilityRequestDTO;
import com.portafolio.citas.dto.AvailabilityResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de disponibilidades y cálculo dinámico de turnos libres.
 */
public interface AvailabilityService {

    AvailabilityResponseDTO saveOrUpdateAvailability(AvailabilityRequestDTO dto, String userEmail);

    List<AvailabilityResponseDTO> getAllAvailabilities();

    List<LocalTime> getAvailableSlots(LocalDate date);
}
