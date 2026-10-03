import React, { useState, useEffect } from 'react';
import availabilityService from '../services/availabilityService';
import appointmentService from '../services/appointmentService';
import { useAuth } from '../context/AuthContext';
import { Calendar, Clock, CheckCircle2, AlertCircle, Sparkles, CalendarDays } from 'lucide-react';

export default function AppointmentForm({ onAppointmentCreated }) {
  const { user } = useAuth();
  
  // Generar los próximos 14 días a partir de hoy
  const generateDaysList = () => {
    const list = [];
    const today = new Date();
    for (let i = 0; i < 14; i++) {
      const d = new Date(today);
      d.setDate(today.getDate() + i);
      
      const year = d.getFullYear();
      const month = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      const dateString = `${year}-${month}-${day}`;

      // Formato corto (Lun, Mar...)
      const shortDay = d.toLocaleDateString('es-ES', { weekday: 'short' });
      const dayNum = String(d.getDate()).padStart(2, '0');
      const monthName = d.toLocaleDateString('es-ES', { month: 'short' });

      list.push({
        dateString,
        shortDay: shortDay.charAt(0).toUpperCase() + shortDay.slice(1, 3),
        displayDate: `${dayNum} ${monthName.charAt(0).toUpperCase() + monthName.slice(1, 4)}`,
      });
    }
    return list;
  };

  const daysList = generateDaysList();
  const [selectedDate, setSelectedDate] = useState(daysList[0].dateString);
  const [availableSlots, setAvailableSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [notes, setNotes] = useState('');
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  // Efecto para consultar turnos libres cada vez que cambie la fecha seleccionada
  useEffect(() => {
    let isMounted = true;
    const fetchSlots = async () => {
      setSelectedSlot(null);
      setLoadingSlots(true);
      setErrorMessage('');
      
      try {
        const slots = await availabilityService.getAvailableSlots(selectedDate);
        if (isMounted) {
          setAvailableSlots(slots || []);
        }
      } catch (err) {
        console.error('Error al cargar turnos disponibles:', err);
        if (isMounted) {
          setAvailableSlots([]);
          setErrorMessage('No se pudo obtener la disponibilidad para esta fecha.');
        }
      } finally {
        if (isMounted) {
          setLoadingSlots(false);
        }
      }
    };

    if (selectedDate) {
      fetchSlots();
    }

    return () => {
      isMounted = false;
    };
  }, [selectedDate]);

  // Separar slots en Mañana (< 13:00) y Tarde (>= 13:00)
  const morningSlots = availableSlots.filter((slot) => {
    const hour = parseInt(slot.split(':')[0], 10);
    return hour < 13;
  });

  const afternoonSlots = availableSlots.filter((slot) => {
    const hour = parseInt(slot.split(':')[0], 10);
    return hour >= 13;
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedDate || !selectedSlot) return;

    setErrorMessage('');
    setSuccessMessage('');
    setSubmitting(true);

    try {
      const timeClean = selectedSlot.length === 5 ? `${selectedSlot}:00` : selectedSlot;
      const appointmentDateTime = `${selectedDate}T${timeClean}`;

      await appointmentService.createAppointment({
        appointmentDateTime,
        notes: notes.trim() ? notes : null,
      });

      setSuccessMessage('¡Cita agendada y confirmada con éxito!');
      setSelectedSlot(null);
      setNotes('');
      
      const refreshedSlots = await availabilityService.getAvailableSlots(selectedDate);
      setAvailableSlots(refreshedSlots || []);

      if (onAppointmentCreated) {
        onAppointmentCreated();
      }
      setTimeout(() => setSuccessMessage(''), 5000);
    } catch (err) {
      console.error(err);
      const errorMsg = err.response?.data?.message || 'Conflicto de horario o error al procesar la reserva.';
      setErrorMessage(typeof errorMsg === 'string' ? errorMsg : 'Error al confirmar la reserva.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="bg-white dark:bg-gray-800/90 border border-slate-200 dark:border-gray-700 text-slate-900 dark:text-white shadow-sm dark:shadow-xl rounded-3xl p-6 md:p-8 space-y-6 transition-colors">
      
      {/* Cabecera Moderna */}
      <div className="border-b border-slate-200 dark:border-gray-700 pb-5">
        <div className="flex items-center space-x-2 text-indigo-600 dark:text-indigo-400 text-xs font-bold uppercase tracking-wider mb-1">
          <CalendarDays className="w-4 h-4" />
          <span>Sistema de Turnos</span>
        </div>
        <h2 className="text-2xl font-black tracking-tight text-slate-900 dark:text-white">Reserva de Turnos Online</h2>
        <p className="text-slate-500 dark:text-gray-400 text-xs sm:text-sm mt-1">
          Seleccione el día de su preferencia y el horario disponible que mejor se adapte a su rutina.
        </p>
      </div>

      {/* Alertas */}
      {errorMessage && (
        <div className="bg-red-500/10 border border-red-500 text-red-600 dark:text-red-400 px-4 py-3 rounded-2xl text-sm flex items-center space-x-2">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {successMessage && (
        <div className="bg-green-500/10 border border-green-500 text-green-600 dark:text-green-400 px-4 py-3 rounded-2xl text-sm flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 flex-shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        
        {/* Selector de Días Horizontal Embebido (Day Strip Carousel) */}
        <div>
          <label className="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-3 flex items-center space-x-2">
            <Calendar className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
            <span>Seleccione el Día (Próximos 14 días)</span>
          </label>
          
          <div className="flex overflow-x-auto gap-3 py-2 scrollbar-thin pb-3">
            {daysList.map((item) => {
              const isSelected = selectedDate === item.dateString;
              return (
                <button
                  key={item.dateString}
                  type="button"
                  onClick={() => setSelectedDate(item.dateString)}
                  className={`flex flex-col items-center justify-center p-3 min-w-[85px] rounded-2xl border transition-all cursor-pointer flex-shrink-0 ${
                    isSelected
                      ? 'bg-indigo-600 text-white font-bold shadow-lg shadow-indigo-600/30 border border-indigo-500 ring-2 ring-indigo-400/50 scale-105'
                      : 'bg-slate-100 text-slate-700 border border-slate-200 hover:bg-slate-200 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-700/80'
                  }`}
                >
                  <span className="text-xs uppercase tracking-wider font-semibold opacity-80">{item.shortDay}</span>
                  <span className="text-sm font-black mt-1">{item.displayDate}</span>
                </button>
              );
            })}
          </div>
        </div>

        {/* Selector de Horarios Disponibles (Slots Dinámicos) */}
        <div>
          <label className="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-2 flex items-center space-x-2">
            <Clock className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
            <span>Horarios Libres para el {selectedDate}</span>
          </label>

          {loadingSlots ? (
            <div className="bg-slate-50 dark:bg-gray-900 rounded-2xl p-8 text-center text-slate-500 dark:text-gray-400 text-sm border border-slate-200 dark:border-gray-700/50">
              <div className="inline-block animate-spin rounded-full h-6 w-6 border-2 border-indigo-600 dark:border-indigo-500 border-t-transparent mb-2"></div>
              <p>Consultando disponibilidad clínica en tiempo real...</p>
            </div>
          ) : availableSlots.length === 0 ? (
            <div className="bg-slate-50 border border-slate-200 text-slate-600 dark:bg-gray-800/80 dark:border-gray-700 dark:text-gray-400 p-6 rounded-2xl text-center space-y-1">
              <p className="font-semibold text-amber-600 dark:text-amber-300 text-sm">No hay turnos disponibles para esta fecha o el consultorio se encuentra cerrado.</p>
              <p className="text-xs text-slate-500 dark:text-gray-500">Por favor, seleccione otro día en la barra superior.</p>
            </div>
          ) : (
            <div className="space-y-4 max-h-[260px] overflow-y-auto pr-1">
              {/* Bloque Mañana */}
              {morningSlots.length > 0 && (
                <div>
                  <span className="text-xs font-bold text-slate-500 dark:text-gray-400 uppercase tracking-wider block mb-2">☀️ Bloque Mañana</span>
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                    {morningSlots.map((slot) => {
                      const timeFormatted = slot.length === 8 ? slot.slice(0, 5) : slot;
                      const isSelected = selectedSlot === slot;
                      return (
                        <button
                          key={slot}
                          type="button"
                          onClick={() => setSelectedSlot(slot)}
                          className={`py-2.5 px-3 rounded-xl text-sm font-mono font-semibold transition-all border cursor-pointer flex items-center justify-center space-x-1.5 ${
                            isSelected
                              ? 'bg-indigo-600 border-indigo-400 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-400/50 scale-102 font-bold'
                              : 'bg-slate-100 text-slate-800 border border-slate-200 hover:bg-slate-200 dark:bg-gray-800 dark:text-gray-200 dark:border-gray-700 dark:hover:bg-gray-700'
                          }`}
                        >
                          {isSelected && <CheckCircle2 className="w-3.5 h-3.5 text-white" />}
                          <span>{timeFormatted}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}

              {/* Bloque Tarde */}
              {afternoonSlots.length > 0 && (
                <div className="pt-2">
                  <span className="text-xs font-bold text-slate-500 dark:text-gray-400 uppercase tracking-wider block mb-2">🌙 Bloque Tarde</span>
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                    {afternoonSlots.map((slot) => {
                      const timeFormatted = slot.length === 8 ? slot.slice(0, 5) : slot;
                      const isSelected = selectedSlot === slot;
                      return (
                        <button
                          key={slot}
                          type="button"
                          onClick={() => setSelectedSlot(slot)}
                          className={`py-2.5 px-3 rounded-xl text-sm font-mono font-semibold transition-all border cursor-pointer flex items-center justify-center space-x-1.5 ${
                            isSelected
                              ? 'bg-indigo-600 border-indigo-400 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-400/50 scale-102 font-bold'
                              : 'bg-slate-100 text-slate-800 border border-slate-200 hover:bg-slate-200 dark:bg-gray-800 dark:text-gray-200 dark:border-gray-700 dark:hover:bg-gray-700'
                          }`}
                        >
                          {isSelected && <CheckCircle2 className="w-3.5 h-3.5 text-white" />}
                          <span>{timeFormatted}</span>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          )}

          <div className="mt-3 text-xs text-indigo-700 dark:text-indigo-300 bg-indigo-50 dark:bg-indigo-500/10 px-3.5 py-2.5 rounded-xl border border-indigo-200 dark:border-indigo-500/20 flex items-center space-x-2">
            <Sparkles className="w-4 h-4 flex-shrink-0" />
            <span>Protocolo de Atención Segura: Llegar con 10 min de anticipación.</span>
          </div>
        </div>

        {/* Motivo de Consulta / Notas */}
        <div>
          <label className="block text-sm font-semibold text-slate-700 dark:text-gray-300 mb-1">Motivo de Consulta u Observaciones</label>
          <textarea
            rows="2"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Describa brevemente el motivo de su visita (opcional)..."
            className="w-full bg-slate-50 border border-slate-200 text-slate-900 placeholder:text-slate-400 dark:bg-gray-900/60 dark:border-gray-700 dark:text-white dark:placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 rounded-2xl px-4 py-3 resize-none text-sm transition-colors"
          />
        </div>

        {/* Tarjeta de Resumen y Botón de Confirmación */}
        <div className="bg-slate-50 border border-slate-200 dark:bg-gray-900/50 dark:border-gray-700/50 text-slate-700 dark:text-gray-300 rounded-2xl p-4 space-y-3 transition-colors">
          <div className="flex justify-between items-center text-xs text-slate-500 dark:text-gray-400">
            <span>Paciente: <strong className="text-slate-900 dark:text-white">{user?.email}</strong></span>
            <span>Fecha seleccionada: <strong className="text-indigo-600 dark:text-indigo-400">{selectedDate}</strong></span>
          </div>
          <div className="flex justify-between items-center text-sm">
            <span className="text-slate-700 dark:text-gray-300 font-medium">Horario elegido:</span>
            <span className="font-mono font-bold text-green-600 dark:text-green-400">
              {selectedSlot ? selectedSlot.slice(0, 5) : 'Pendiente de selección'}
            </span>
          </div>

          <button
            type="submit"
            disabled={!selectedDate || !selectedSlot || submitting}
            className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-semibold py-3.5 rounded-2xl transition-all shadow-lg shadow-indigo-600/30 disabled:opacity-50 cursor-pointer mt-2"
          >
            {submitting ? 'Confirmando Reserva...' : 'Confirmar Reserva de Cita'}
          </button>
        </div>

      </form>
    </div>
  );
}
