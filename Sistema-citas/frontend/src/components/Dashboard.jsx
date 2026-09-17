import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import appointmentService from '../services/appointmentService';
import AppointmentForm from './AppointmentForm';

export default function Dashboard() {
  const { user, logout, isAdmin } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
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

  const getStatusBadge = (status) => {
    switch (status) {
      case 'PENDING':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-yellow-500/20 text-yellow-400 border border-yellow-500/30">PENDING</span>;
      case 'CONFIRMED':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-green-500/20 text-green-400 border border-green-500/30">CONFIRMED</span>;
      case 'CANCELLED':
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-red-500/20 text-red-400 border border-red-500/30">CANCELLED</span>;
      default:
        return <span className="px-2.5 py-1 text-xs font-semibold rounded-full bg-gray-500/20 text-gray-400">{status}</span>;
    }
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      {/* Navbar */}
      <nav className="bg-gray-800 border-b border-gray-700 px-6 py-4 flex justify-between items-center shadow-md">
        <div className="flex items-center space-x-3">
          <span className="text-xl font-bold bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent">
            Sistema de Citas
          </span>
          <span className="text-xs px-2.5 py-1 rounded-full bg-indigo-500/20 text-indigo-300 font-medium border border-indigo-500/30">
            {user?.role}
          </span>
        </div>
        <div className="flex items-center space-x-4">
          <span className="text-sm text-gray-300">{user?.email}</span>
          <button
            onClick={logout}
            className="bg-red-600/20 hover:bg-red-600 text-red-400 hover:text-white px-4 py-2 rounded-xl text-sm font-semibold transition-all border border-red-500/30"
          >
            Cerrar Sesión
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Formulario a la izquierda */}
          <div className="lg:col-span-1">
            <AppointmentForm onAppointmentCreated={fetchAppointments} />
          </div>

          {/* Listado de citas a la derecha */}
          <div className="lg:col-span-2 bg-gray-800 rounded-2xl shadow-xl p-6 border border-gray-700">
            <h3 className="text-xl font-bold text-white mb-6">
              {isAdmin ? 'Todas las Citas del Sistema' : 'Mis Citas Reservadas'}
            </h3>

            {error && <p className="text-red-400 text-sm mb-4">{error}</p>}

            {loading ? (
              <p className="text-gray-400 text-center py-8">Cargando citas...</p>
            ) : appointments.length === 0 ? (
              <p className="text-gray-400 text-center py-8">No hay citas registradas.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-gray-700 text-gray-400 text-xs uppercase tracking-wider">
                      <th className="py-3 px-3">Cliente</th>
                      <th className="py-3 px-3">Fecha y Hora</th>
                      <th className="py-3 px-3">Estado</th>
                      <th className="py-3 px-3">Notas</th>
                      <th className="py-3 px-3 text-right">Acciones</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-gray-700/50 text-sm">
                    {appointments.map((apt) => (
                      <tr key={apt.id} className="hover:bg-gray-700/30 transition-all">
                        <td className="py-3 px-3 font-medium text-white">
                          <div className="truncate max-w-[140px]" title={apt.clientName}>{apt.clientName}</div>
                          <div className="text-xs text-gray-400 truncate max-w-[140px]" title={apt.userEmail}>{apt.userEmail}</div>
                        </td>
                        <td className="py-3 px-3 text-gray-300 text-xs whitespace-nowrap">
                          {new Date(apt.appointmentDateTime).toLocaleString()}
                        </td>
                        <td className="py-3 px-3 whitespace-nowrap">{getStatusBadge(apt.status)}</td>
                        <td className="py-3 px-3 text-gray-400 max-w-[120px] truncate" title={apt.notes || ''}>
                          {apt.notes || '-'}
                        </td>
                        <td className="py-3 px-3 text-right whitespace-nowrap">
                          {apt.status !== 'CANCELLED' && (isAdmin || apt.userEmail === user?.email) && (
                            <button
                              onClick={() => handleCancel(apt.id)}
                              className="bg-red-500/10 hover:bg-red-600 text-red-400 hover:text-white px-3 py-1.5 rounded-lg text-xs font-medium transition-all border border-red-500/20"
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
        </div>
      </main>
    </div>
  );
}
