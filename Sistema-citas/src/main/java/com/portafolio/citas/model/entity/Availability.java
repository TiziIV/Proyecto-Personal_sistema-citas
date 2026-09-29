package com.portafolio.citas.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Entidad JPA que representa la disponibilidad horaria configurable por los administradores (tabla "availabilities").
 * Define los días de la semana, horarios de apertura/cierre y la duración en minutos de cada turno (slot).
 */
@Entity
@Table(name = "availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek; // Día de la semana (MONDAY, TUESDAY, etc.)

    @Column(nullable = false)
    private LocalTime startTime; // Hora de inicio de la jornada de atención

    @Column(nullable = false)
    private LocalTime endTime; // Hora de cierre de la jornada de atención

    @Column(nullable = false)
    @Builder.Default
    private Integer slotDurationMinutes = 30; // Duración estándar de cada turno en minutos (ej. 30 min)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Administrador que configuró esta disponibilidad
}
