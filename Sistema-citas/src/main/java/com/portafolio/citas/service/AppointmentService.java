package com.portafolio.citas.service;

import com.portafolio.citas.dto.AppointmentRequestDTO;
import com.portafolio.citas.dto.AppointmentResponseDTO;

import java.util.List;

/**
 * Interfaz de la capa de servicio para la gestión de citas.
 */
public interface AppointmentService {

    /**
     * Crea una nueva cita vinculada al usuario autenticado tras validar reglas de negocio.
     * 
     * @param requestDTO Datos de la cita solicitada.
     * @param userEmail Email del usuario autenticado (extraído del token JWT).
     * @return AppointmentResponseDTO con los datos de la cita creada.
     */
    AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO, String userEmail);

    /**
     * Obtiene el listado completo de todas las citas registradas en el sistema (ideal para administradores).
     * 
     * @return Lista de AppointmentResponseDTO.
     */
    List<AppointmentResponseDTO> getAllAppointments();

    /**
     * Obtiene exclusivamente el listado de citas pertenecientes al usuario autenticado.
     * 
     * @param userEmail Email del usuario autenticado.
     * @return Lista de AppointmentResponseDTO del usuario.
     */
    List<AppointmentResponseDTO> getMyAppointments(String userEmail);

    /**
     * Busca y retorna una cita específica según su ID único.
     * 
     * @param id Identificador de la cita.
     * @return AppointmentResponseDTO con los datos de la cita encontrada.
     */
    AppointmentResponseDTO getAppointmentById(Long id);

    /**
     * Cancela una cita existente cambiando su estado a CANCELLED (Soft Delete).
     * 
     * @param id Identificador de la cita a cancelar.
     * @return AppointmentResponseDTO con la cita actualizada.
     */
    AppointmentResponseDTO cancelAppointment(Long id);
}
