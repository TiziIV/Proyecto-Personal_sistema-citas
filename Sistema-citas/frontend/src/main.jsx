import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { AuthProvider } from './context/AuthContext'

/**
 * Punto de entrada principal de la aplicación React.
 * 
 * ¿Por qué el AuthProvider debe colocarse en el punto más alto del árbol de componentes?
 * - El sistema de Context API de React funciona de arriba hacia abajo (top-down). 
 * - Al envolver el componente raíz `<App />` (y por ende a toda la aplicación) dentro de `<AuthProvider>`,
 *   garantizamos que cualquier componente hijo o nieto (como páginas, formularios, barras de navegación o rutas protegidas)
 *   pueda consumir el contexto de autenticación mediante el hook `useAuth()` sin importar cuán profundo se encuentre en el árbol de componentes.
 */
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </StrictMode>,
)
