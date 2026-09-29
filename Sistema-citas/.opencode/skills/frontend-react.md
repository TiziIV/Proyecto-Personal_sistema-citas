# Skill: Frontend React Modular & State Management

Esta habilidad define las pautas para el desarrollo de la interfaz de usuario en React 19 con Vite y Tailwind CSS v4 para el proyecto "Sistema-citas".

## ⚛️ Directrices de Desarrollo Frontend
1. **Componentes Modulares y Responsivos:**
   - Estructura limpia de componentes reutilizables (`AuthForm`, `AppointmentForm`, `Dashboard`).
   - Estilizado moderno utilizando clases utilitarias de **Tailwind CSS v4** con soporte para modo oscuro e interfaces adaptativas.

2. **Gestión Global de Sesión (Context API):**
   - Uso de `AuthContext` y el hook personalizado `useAuth()` para centralizar el estado de autenticación (`user`, `token`, `role`, `isAuthenticated`, `isAdmin`).
   - Prevención del *Prop Drilling* permitiendo acceso directo a la sesión desde cualquier componente de la jerarquía.

3. **Cliente HTTP Seguro con Axios:**
   - Instancia base centralizada en `api.js` con `baseURL` apuntando a `http://localhost:8080/api`.
   - **Request Interceptor:** Inyección automática de la cabecera `Authorization: Bearer <token>` obtenida desde `localStorage`.
   - **Response Interceptor:** Gestión inteligente de errores HTTP (limpieza de sesión y redirección en errores `401`, y propagación limpia de errores `403` para advertencias visuales en pantalla).
