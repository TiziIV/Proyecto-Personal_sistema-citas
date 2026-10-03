import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

export default function AuthForm() {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login, register } = useAuth();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        await login({ email: formData.email, password: formData.password });
      } else {
        await register({
          fullName: formData.fullName,
          email: formData.email,
          password: formData.password,
          role: 'ROLE_CLIENT',
        });
      }
    } catch (err) {
      console.error(err);
      const errorMsg =
        err.response?.data?.message ||
        err.response?.data?.error ||
        'Ocurrió un error. Verifique sus datos.';
      setError(typeof errorMsg === 'string' ? errorMsg : 'Error en la autenticación.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-gray-900 flex items-center justify-center px-4 py-12 transition-colors duration-300">
      <div className="max-w-md w-full bg-white dark:bg-gray-800 rounded-3xl shadow-xl p-8 border border-slate-200 dark:border-gray-700 transition-colors duration-300">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-extrabold text-slate-800 dark:text-white">Sistema de Citas</h2>
          <p className="text-slate-500 dark:text-gray-400 mt-2">
            {isLogin ? 'Inicia sesión en tu cuenta' : 'Crea una nueva cuenta'}
          </p>
        </div>

        {/* Tabs de alternancia */}
        <div className="flex bg-slate-100 dark:bg-gray-900 rounded-xl p-1 mb-6 border border-slate-200 dark:border-gray-700">
          <button
            type="button"
            onClick={() => { setIsLogin(true); setError(''); }}
            className={`flex-1 py-2 text-sm font-medium rounded-lg transition-all cursor-pointer ${
              isLogin ? 'bg-indigo-600 text-white shadow' : 'text-slate-600 dark:text-gray-400 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            Iniciar Sesión
          </button>
          <button
            type="button"
            onClick={() => { setIsLogin(false); setError(''); }}
            className={`flex-1 py-2 text-sm font-medium rounded-lg transition-all cursor-pointer ${
              !isLogin ? 'bg-indigo-600 text-white shadow' : 'text-slate-600 dark:text-gray-400 hover:text-slate-900 dark:hover:text-white'
            }`}
          >
            Registrarse
          </button>
        </div>

        {error && (
          <div className="bg-red-500/10 border border-red-500 text-red-500 dark:text-red-400 px-4 py-3 rounded-xl mb-6 text-sm text-center">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          {!isLogin && (
            <div>
              <label className="block text-sm font-medium text-slate-700 dark:text-gray-300 mb-1">Nombre Completo</label>
              <input
                type="text"
                name="fullName"
                required={!isLogin}
                value={formData.fullName}
                onChange={handleChange}
                placeholder="Juan Pérez"
                className="w-full bg-slate-50 dark:bg-gray-900 border border-slate-200 dark:border-gray-700 rounded-xl px-4 py-3 text-slate-800 dark:text-white focus:outline-none focus:border-indigo-500 transition-colors duration-300"
              />
            </div>
          )}

          <div>
            <label className="block text-sm font-medium text-slate-700 dark:text-gray-300 mb-1">Correo Electrónico</label>
            <input
              type="email"
              name="email"
              required
              value={formData.email}
              onChange={handleChange}
              placeholder="tu@correo.com"
              className="w-full bg-slate-50 dark:bg-gray-900 border border-slate-200 dark:border-gray-700 rounded-xl px-4 py-3 text-slate-800 dark:text-white focus:outline-none focus:border-indigo-500 transition-colors duration-300"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 dark:text-gray-300 mb-1">Contraseña</label>
            <input
              type="password"
              name="password"
              required
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              className="w-full bg-slate-50 dark:bg-gray-900 border border-slate-200 dark:border-gray-700 rounded-xl px-4 py-3 text-slate-800 dark:text-white focus:outline-none focus:border-indigo-500 transition-colors duration-300"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-3 rounded-xl transition-all shadow-lg shadow-indigo-600/30 disabled:opacity-50 cursor-pointer"
          >
            {loading ? 'Procesando...' : isLogin ? 'Iniciar Sesión' : 'Registrarse'}
          </button>
        </form>
      </div>
    </div>
  );
}
