package com.portafolio.citas.service;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;

import java.util.List;

/**
 * Interfaz de la capa de servicio para la gestión de citas.
 * 
 * ¿Por qué desacoplamos la definición del servicio usando una interfaz frente a su implementación directa?
 * 1. Principio de Inversión de Dependencias (SOLID): Los controladores y otros componentes dependen de abstracciones (interfaces)
 *    y no de implementaciones concretas, facilitando el desacoplamiento.
 * 2. Polimorfismo y Múltiples Implementaciones: Permite crear diferentes implementaciones si fuera necesario
 *    (por ejemplo, una implementación JPA y otra basada en MongoDB o APIs externas) sin romper la arquitectura.
 * 3. Pruebas Unitarias (Mocking): Facilita la creación de mocks (con Mockito) para probar los controladores 
 *    aisladamente sin depender de la lógica real de la base de datos.
 */
public interface AppointmentService {

    /**
     * Crea una nueva cita tras validar reglas de negocio (fecha futura y disponibilidad de horario).
     * 
     * @param requestDTO Datos de la cita solicitada por el cliente.
     * @return AppointmentResponseDTO con los datos de la cita creada y su estado inicial.
     */
    AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO);

    /**
     * Obtiene el listado completo de todas las citas registradas en el sistema.
     * 
     * @return Lista de AppointmentResponseDTO.
     */
    List<AppointmentResponseDTO> getAllAppointments();

    /**
     * Busca y retorna una cita específica según su ID único.
     * 
     * @param id Identificador de la cita.
     * @return AppointmentResponseDTO con los datos de la cita encontrada.
     */
    AppointmentResponseDTO getAppointmentById(Long id);

    /**
     * Cancela una cita existente cambiando su estado a CANCELLED.
     * 
     * @param id Identificador de la cita a cancelar.
     * @return AppointmentResponseDTO con la cita actualizada.
     */
    AppointmentResponseDTO cancelAppointment(Long id);
}
