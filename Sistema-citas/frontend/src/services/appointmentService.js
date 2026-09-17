import api from './api';

/**
 * Servicio para la gestión de citas y reservas conectándose con /api/appointments.
 */
const appointmentService = {
  /**
   * Obtiene exclusivamente las citas del usuario autenticado.
   */
  getMyAppointments: async () => {
    const response = await api.get('/appointments/my-appointments');
    return response.data;
  },

  /**
   * Obtiene el listado completo de todas las citas del sistema (Uso administrativo).
   */
  getAllAppointments: async () => {
    const response = await api.get('/appointments');
    return response.data;
  },

  /**
   * Crea una nueva cita.
   * @param {Object} data - Objeto conteniendo { appointmentDateTime, notes, clientName, clientEmail }
   */
  createAppointment: async (data) => {
    const response = await api.post('/appointments', data);
    return response.data;
  },

  /**
   * Cancela lógicamente (Soft Delete) una cita por su ID.
   * @param {Long} id - Identificador de la cita.
   */
  cancelAppointment: async (id) => {
    const response = await api.delete(`/appointments/${id}`);
    return response.data;
  },
};

export default appointmentService;
