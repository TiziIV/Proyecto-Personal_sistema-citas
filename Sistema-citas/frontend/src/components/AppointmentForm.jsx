import React, { useState } from 'react';
import appointmentService from '../services/appointmentService';

export default function AppointmentForm({ onAppointmentCreated }) {
  const [appointmentDateTime, setAppointmentDateTime] = useState('');
  const [notes, setNotes] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  // Obtener fecha actual en formato ISO para el atributo min (YYYY-MM-DDTHH:mm)
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  const minDateTime = now.toISOString().slice(0, 16);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      // Formatear LocalDateTime para enviar al backend (ej: "2026-05-10T10:00:00")
      const formattedDateTime = appointmentDateTime.length === 16 ? `${appointmentDateTime}:00` : appointmentDateTime;

      await appointmentService.createAppointment({
        appointmentDateTime: formattedDateTime,
        notes: notes.trim() ? notes : null,
      });

      setSuccess('¡Cita agendada exitosamente!');
      setAppointmentDateTime('');
      setNotes('');
      if (onAppointmentCreated) {
        onAppointmentCreated();
      }
    } catch (err) {
      console.error(err);
      const errorMsg =
        err.response?.data?.message ||
        'Conflicto de horario o error al agendar la cita.';
      setError(typeof errorMsg === 'string' ? errorMsg : 'Error al agendar cita.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-gray-800 rounded-2xl shadow-xl p-6 border border-gray-700">
      <h3 className="text-xl font-bold text-white mb-4">Agendar Nueva Cita</h3>

      {error && (
        <div className="bg-red-500/10 border border-red-500 text-red-400 px-4 py-3 rounded-xl mb-4 text-sm">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-500/10 border border-green-500 text-green-400 px-4 py-3 rounded-xl mb-4 text-sm">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Fecha y Hora</label>
          <input
            type="datetime-local"
            required
            min={minDateTime}
            value={appointmentDateTime}
            onChange={(e) => setAppointmentDateTime(e.target.value)}
            className="w-full bg-gray-900 border border-gray-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-indigo-500"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-300 mb-1">Notas (Opcional)</label>
          <textarea
            rows="3"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Motivo de la consulta..."
            className="w-full bg-gray-900 border border-gray-700 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-indigo-500 resize-none"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-3 rounded-xl transition-all shadow-lg shadow-indigo-600/30 disabled:opacity-50"
        >
          {loading ? 'Agendando...' : 'Confirmar Reserva'}
        </button>
      </form>
    </div>
  );
}
