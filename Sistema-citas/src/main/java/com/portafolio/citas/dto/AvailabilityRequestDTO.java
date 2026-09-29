package com.portafolio.citas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO para la creación o actualización de la configuración de disponibilidad horaria.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRequestDTO {

    @NotNull(message = "El día de la semana es obligatorio")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;

    @NotNull(message = "La duración del turno es obligatoria")
    @Min(value = 10, message = "La duración mínima del turno debe ser de 10 minutos")
    @Max(value = 120, message = "La duración máxima del turno debe ser de 120 minutos")
    private Integer slotDurationMinutes;
}
