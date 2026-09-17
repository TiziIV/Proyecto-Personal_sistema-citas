import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

/**
 * Contexto global de Autenticación para la aplicación React.
 * 
 * ¿Por qué usar Context API en lugar de pasar props manualmente a través de múltiples componentes (Prop Drilling)?
 * 1. Evita Prop Drilling: Permite que cualquier componente de la jerarquía (como Navbar, Dashboard, Formularios de Citas) 
 *    acceda directamente al usuario autenticado, estado de sesión y funciones de login/logout sin necesidad 
 *    de transferir props nivel por nivel a través de componentes intermedios que no las necesitan.
 * 2. Estado Centralizado: Mantiene una única fuente de verdad para la sesión del usuario, sincronizándose 
 *    automáticamente con `localStorage` y facilitando la protección de rutas o la renderización condicional.
 */
const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Al montar la app, verificamos si hay un usuario logueado en localStorage
    const currentUser = authService.getCurrentUser();
    if (currentUser) {
      setUser(currentUser);
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const data = await authService.login(credentials);
    setUser({
      token: data.token,
      email: data.email,
      role: data.role,
    });
    return data;
  };

  const register = async (userData) => {
    const data = await authService.register(userData);
    setUser({
      token: data.token,
      email: data.email,
      role: data.role,
    });
    return data;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const value = {
    user,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ROLE_ADMIN',
    login,
    register,
    logout,
  };

  if (loading) {
    return <div className="flex justify-center items-center h-screen">Cargando sesión...</div>;
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

/**
 * Hook personalizado para consumir el contexto de autenticación fácilmente.
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser utilizado dentro de un AuthProvider');
  }
  return context;
};
