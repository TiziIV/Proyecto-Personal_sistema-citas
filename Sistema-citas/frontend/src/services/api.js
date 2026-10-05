import axios from 'axios';

/**
 * Cliente HTTP base configurado con Axios para interactuar con la API REST de Spring Boot.
 *
 * - baseURL: Dinámica según el entorno. En producción toma VITE_API_URL inyectada por el hosting.
 *   En desarrollo local toma por defecto http://localhost:8080/api.
 *
 * Comportamiento del interceptor de respuesta:
 * - 401 Unauthorized: El token ha expirado o no es válido. Se limpia el localStorage y se redirige al login.
 * - 403 Forbidden: El usuario está autenticado pero no tiene los privilegios necesarios (ej. un cliente intentando cancelar una cita).
 *   No se desloguea al usuario; el error se propaga para que el componente visual pueda mostrar una advertencia en pantalla.
 */
const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor de Solicitud (Request Interceptor)
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor de Respuesta (Response Interceptor)
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            if (error.response.status === 401) {
                // Solo limpiamos sesión y redirigimos si es 401 (No autenticado / Token vencido)
                localStorage.removeItem('token');
                localStorage.removeItem('email');
                localStorage.removeItem('role');

                if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
                    window.location.href = '/login';
                }
            }
            // Si es 403 (Forbidden), NO deslogueamos; dejamos propagar el error para que el componente lo muestre en pantalla.
        }
        return Promise.reject(error);
    }
);

export default api;