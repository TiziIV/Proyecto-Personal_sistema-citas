import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import appointmentService from '../services/appointmentService';
import AppointmentForm from './AppointmentForm';
import AdminSchedule from './AdminSchedule';
import { Calendar, Clock, ClipboardList, Shield, LogOut, Sun, Moon } from 'lucide-react';

export default function Dashboard() {
  const { user, logout, isAdmin } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const [activeTab, setActiveTab] = useState(isAdmin ? 'schedule' : 'book');
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const data = isAdmin ? await appointmentService.getAllAppointments() : await appointmentService.getMyAppointments();
      setAppointments(data);
    } catch (err) {
      console.error(err);
      setError('Error al cargar las citas.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, [isAdmin]);

  const handleCancel = async (id) => {
    if (!window.confirm('¿Está seguro de que desea cancelar esta cita?')) return;
    try {
      await appointmentService.cancelAppointment(id);
      fetchAppointments();
    } catch (err) {
      console.error(err);
      const errorMsg = err.response?.data?.message || 'No se pudo cancelar la cita.';
      alert(errorMsg);
    }
  };

  const getRoleLabel = (role) => {
    if (role === 'ROLE_ADMIN') return 'Administrador';
    if (role === 'ROLE_CLIENT') return 'Paciente';
    return role || 'Usuario';
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-yellow-500/20 text-yellow-600 dark:text-yellow-400 border border-yellow-500/30">PENDING</span>;
      case 'CONFIRMED':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-green-500/20 text-green-600 dark:text-green-400 border border-green-500/30">CONFIRMED</span>;
      case 'CANCELLED':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-red-500/20 text-red-600 dark:text-red-400 border border-red-500/30">CANCELLED</span>;
      default:
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-slate-200 dark:bg-gray-500/20 text-slate-700 dark:text-gray-400">{status}</span>;
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 text-slate-800 dark:bg-gray-900 dark:text-white transition-colors duration-200">
      {/* Navbar Superior */}
      <nav className="bg-white border-b border-slate-200 dark:bg-gray-800 dark:border-gray-700 px-6 py-4 flex flex-col sm:flex-row justify-between items-center shadow-sm dark:shadow-md gap-4 transition-colors">
        <div className="flex items-center space-x-3">
          <span className="text-xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 dark:from-indigo-400 dark:to-purple-400 bg-clip-text text-transparent">
            Sistema de Citas
          </span>
          <span className="text-xs px-2.5 py-1 rounded-full bg-indigo-500/10 dark:bg-indigo-500/20 text-indigo-600 dark:text-indigo-300 font-medium border border-indigo-500/20 dark:border-indigo-500/30 flex items-center space-x-1">
            <Shield className="w-3 h-3 mr-1" />
            {getRoleLabel(user?.role)}
          </span>
        </div>

        {/* Pestañas de Navegación */}
        <div className="flex bg-slate-200 dark:bg-gray-900 rounded-2xl p-1.5 border border-slate-300 dark:border-gray-700">
          {isAdmin ? (
            <>
              <button
                type="button"
                onClick={() => setActiveTab('schedule')}
                className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                  activeTab === 'schedule'
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-600 hover:text-slate-900 dark:text-gray-400 dark:hover:text-white'
                }`}
              >
                <Clock className="w-4 h-4" />
                <span>Gestión de Disponibilidad</span>
              </button>
              <button
                type="button"
                onClick={() => { setActiveTab('appointments'); fetchAppointments(); }}
                className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                  activeTab === 'appointments'
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-600 hover:text-slate-900 dark:text-gray-400 dark:hover:text-white'
                }`}
              >
                <ClipboardList className="w-4 h-4" />
                <span>Todas las Citas del Sistema</span>
              </button>
            </>
          ) : (
            <>
              <button
                type="button"
                onClick={() => setActiveTab('book')}
                className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                  activeTab === 'book'
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-600 hover:text-slate-900 dark:text-gray-400 dark:hover:text-white'
                }`}
              >
                <Calendar className="w-4 h-4" />
                <span>Reservar Cita</span>
              </button>
              <button
                type="button"
                onClick={() => { setActiveTab('appointments'); fetchAppointments(); }}
                className={`flex items-center space-x-2 px-4 py-2 rounded-xl text-xs sm:text-sm font-semibold transition-all cursor-pointer ${
                  activeTab === 'appointments'
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-600 hover:text-slate-900 dark:text-gray-400 dark:hover:text-white'
                }`}
              >
                <ClipboardList className="w-4 h-4" />
                <span>Mis Citas Reservadas</span>
              </button>
            </>
          )}
        </div>

        <div className="flex items-center space-x-4">
          <span className="text-sm text-slate-600 dark:text-gray-300 hidden md:inline">{user?.email}</span>
          
          {/* Theme Switch Button */}
          <button
            type="button"
            onClick={toggleTheme}
            title={theme === 'dark' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
            aria-label={theme === 'dark' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'}
            className="p-2 rounded-xl bg-slate-100 hover:bg-slate-200 dark:bg-gray-700/50 dark:hover:bg-gray-700 text-amber-500 dark:text-indigo-400 transition-colors cursor-pointer border border-slate-200 dark:border-gray-600"
          >
            {theme === 'dark' ? (
              <Sun className="w-5 h-5 text-amber-400" />
            ) : (
              <Moon className="w-5 h-5 text-indigo-600" />
            )}
          </button>

          <button
            type="button"
            onClick={logout}
            className="flex items-center space-x-1 bg-red-500/10 hover:bg-red-600 text-red-600 dark:text-red-400 hover:text-white px-4 py-2 rounded-xl text-sm font-semibold transition-all border border-red-500/25 dark:border-red-500/30 cursor-pointer"
          >
            <LogOut className="w-4 h-4" />
            <span>Salir</span>
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        {activeTab === 'schedule' && isAdmin && (
          <div className="w-full">
            <AdminSchedule />
          </div>
        )}

        {activeTab === 'book' && !isAdmin && (
            <div className="w-full">
              <AppointmentForm
                  onAppointmentCreated={() => {
                    fetchAppointments();
                    setActiveTab('appointments');
                  }}
              />
            </div>
        )}

        {activeTab === 'appointments' && (
          <div className="w-full bg-white dark:bg-gray-800 rounded-3xl shadow-md dark:shadow-xl p-6 md:p-8 border border-slate-200 dark:border-gray-700 transition-colors">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-slate-900 dark:text-white">
                {isAdmin ? 'Todas las Citas del Sistema' : 'Mis Citas Reservadas'}
              </h3>
              <button
                type="button"
                onClick={fetchAppointments}
                className="text-xs bg-slate-100 hover:bg-slate-200 dark:bg-gray-700 dark:hover:bg-gray-600 text-slate-700 dark:text-gray-200 px-3.5 py-2 rounded-xl transition-all cursor-pointer font-medium border border-slate-200 dark:border-gray-600"
              >
                Actualizar Lista
              </button>
            </div>

            {error && <p className="text-red-500 dark:text-red-400 text-sm mb-4">{error}</p>}

            {loading ? (
              <p className="text-slate-500 dark:text-gray-400 text-center py-12">Cargando citas...</p>
            ) : appointments.length === 0 ? (
              <p className="text-slate-500 dark:text-gray-400 text-center py-12">No hay citas registradas.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-gray-700 text-slate-500 dark:text-gray-400 text-xs uppercase tracking-wider">
                      <th className="py-3 px-3">Cliente</th>
                      <th className="py-3 px-3">Fecha y Hora</th>
                      <th className="py-3 px-3">Estado</th>
                      <th className="py-3 px-3">Notas</th>
                      <th className="py-3 px-3 text-right">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200 dark:divide-gray-700/50 text-sm hover:bg-slate-50 dark:hover:bg-gray-700/30 text-slate-700 dark:text-gray-300">
                    {appointments.map((apt) => (
                      <tr key={apt.id} className="hover:bg-slate-50 dark:hover:bg-gray-700/30 transition-all">
                        <td className="py-3.5 px-3 font-medium text-slate-900 dark:text-white">
                          <div className="truncate max-w-[180px]" title={apt.clientName}>{apt.clientName}</div>
                          <div className="text-xs text-slate-500 dark:text-gray-400 truncate max-w-[180px]" title={apt.userEmail}>{apt.userEmail}</div>
                        </td>
                        <td className="py-3.5 px-3 text-slate-600 dark:text-gray-300 text-xs whitespace-nowrap">
                          {new Date(apt.appointmentDateTime).toLocaleString()}
                        </td>
                        <td className="py-3.5 px-3 whitespace-nowrap">{getStatusBadge(apt.status)}</td>
                        <td className="py-3.5 px-3 text-slate-500 dark:text-gray-400 max-w-[200px] truncate" title={apt.notes || ''}>
                          {apt.notes || '-'}
                        </td>
                        <td className="py-3.5 px-3 text-right whitespace-nowrap">
                          {apt.status !== 'CANCELLED' && (isAdmin || apt.userEmail === user?.email) && (
                            <button
                              type="button"
                              onClick={() => handleCancel(apt.id)}
                              className="bg-red-500/10 hover:bg-red-600 text-red-600 dark:text-red-400 hover:text-white px-3 py-1.5 rounded-xl text-xs font-medium transition-all border border-red-500/25 dark:border-red-500/20 cursor-pointer"
                            >
                              Cancelar
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}
