import api from './api';

/**
 * Servicio de Autenticación para interactuar con los endpoints /api/auth.
 */
const authService = {
  /**
   * Inicia sesión con las credenciales del usuario.
   * Almacena el token JWT, email y rol en localStorage si es exitoso.
   */
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('role', response.data.role);
    }
    return response.data;
  },

  /**
   * Registra un nuevo usuario en el sistema.
   */
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('role', response.data.role);
    }
    return response.data;
  },

  /**
   * Cierra sesión eliminando los datos de sesión de localStorage.
   */
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
  },

  /**
   * Retorna los datos básicos del usuario almacenados actualmente en localStorage.
   */
  getCurrentUser: () => {
    const token = localStorage.getItem('token');
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');

    if (!token) return null;

    return { token, email, role };
  },
};

export default authService;
