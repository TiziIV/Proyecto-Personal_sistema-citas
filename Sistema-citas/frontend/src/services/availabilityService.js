import api from './api';

/**
 * Servicio para la gestión de horarios de disponibilidad y consulta de turnos libres.
 * 
 * ¿Cómo se autentican estas peticiones?
 * - Al igual que el resto de nuestros servicios, utilizamos la instancia centralizada de Axios (`api`).
 * - Gracias al interceptor de solicitudes configurado en `api.js`, cualquier llamada (POST, GET) 
 *   incluirá automáticamente el token JWT en la cabecera `Authorization: Bearer <token>` si el usuario ha iniciado sesión,
 *   garantizando que el backend valide permisos (por ejemplo, verificando ROLE_ADMIN al guardar franjas horarias).
 */
const availabilityService = {
  /**
   * Guarda o actualiza la configuración de disponibilidad horaria (Exclusivo ROLE_ADMIN).
   * @param {Object} data - Objeto conteniendo { dayOfWeek, startTime, endTime, slotDurationMinutes }
   */
  saveAvailability: async (data) => {
    const response = await api.post('/availability', data);
    return response.data;
  },

  /**
   * Obtiene todas las configuraciones de disponibilidad horaria del sistema.
   */
  getAvailabilities: async () => {
    const response = await api.get('/availability');
    return response.data;
  },

  /**
   * Obtiene la lista de horarios (slots) disponibles para una fecha específica.
   * @param {String} dateString - Fecha en formato "YYYY-MM-DD"
   */
  getAvailableSlots: async (dateString) => {
    const response = await api.get(`/availability/slots?date=${dateString}`);
    return response.data;
  },
};

export default availabilityService;
