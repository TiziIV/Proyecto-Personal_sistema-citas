import React, { useState, useEffect } from 'react';
import availabilityService from '../services/availabilityService';
import { Clock, Calendar, Check, Plus, Save, AlertCircle, Sparkles, Shield, CheckCircle2 } from 'lucide-react';

const DAYS_MAP = [
  { label: 'Lun', value: 'MONDAY' },
  { label: 'Mar', value: 'TUESDAY' },
  { label: 'Mié', value: 'WEDNESDAY' },
  { label: 'Jue', value: 'THURSDAY' },
  { label: 'Vie', value: 'FRIDAY' },
  { label: 'Sáb', value: 'SATURDAY' },
  { label: 'Dom', value: 'SUNDAY' },
];

export default function AdminSchedule() {
  const [selectedDays, setSelectedDays] = useState(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']);
  const [startTime, setStartTime] = useState('08:30');
  const [endTime, setEndTime] = useState('18:30');
  const [slotDuration, setSlotDuration] = useState(30);
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toastMsg, setToastMsg] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);

  const fetchAvailabilities = async () => {
    try {
      const data = await availabilityService.getAvailabilities();
      setRules(data);
    } catch (err) {
      console.error('Error al cargar disponibilidades:', err);
    }
  };

  useEffect(() => {
    fetchAvailabilities();
  }, []);

  const toggleDay = (dayValue) => {
    if (selectedDays.includes(dayValue)) {
      setSelectedDays(selectedDays.filter((d) => d !== dayValue));
    } else {
      setSelectedDays([...selectedDays, dayValue]);
    }
  };

  const selectWeekdays = () => {
    setSelectedDays(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY']);
  };

  // Calcular slots teóricos simulados para la vista previa
  const calculateSimulatedSlots = () => {
    if (!startTime || !endTime || !slotDuration) return [];
    const slots = [];
    let [startH, startM] = startTime.split(':').map(Number);
    let [endH, endM] = endTime.split(':').map(Number);

    let currentMinutes = startH * 60 + startM;
    const endMinutes = endH * 60 + endM;

    while (currentMinutes + slotDuration <= endMinutes) {
      const h = Math.floor(currentMinutes / 60).toString().padStart(2, '0');
      const m = (currentMinutes % 60).toString().padStart(2, '0');
      slots.push(`${h}:${m}`);
      currentMinutes += slotDuration;
    }
    return slots;
  };

  const simulatedSlots = calculateSimulatedSlots();
  const totalSlotsPerDay = simulatedSlots.length;
  const estimatedWeeklyCapacity = totalSlotsPerDay * selectedDays.length;

  const handleSave = async () => {
    setErrorMsg(null);
    setToastMsg(null);

    if (selectedDays.length === 0) {
      setErrorMsg('Debe seleccionar al menos un día de atención.');
      return;
    }

    if (startTime >= endTime) {
      setErrorMsg('La hora de inicio debe ser anterior a la hora de fin.');
      return;
    }

    setLoading(true);
    try {
      // Enviar en paralelo para cada día seleccionado
      const promises = selectedDays.map((day) =>
        availabilityService.saveAvailability({
          dayOfWeek: day,
          startTime: `${startTime}:00`,
          endTime: `${endTime}:00`,
          slotDurationMinutes: Number(slotDuration),
        })
      );

      await Promise.all(promises);
      setToastMsg('¡Horarios de atención configurados y guardados con éxito en PostgreSQL!');
      fetchAvailabilities();
      setTimeout(() => setToastMsg(null), 5000);
    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || 'Error al guardar la disponibilidad.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 text-slate-800 dark:text-white transition-colors duration-300">
      {/* Toast Notification */}
      {toastMsg && (
        <div className="fixed top-6 right-6 z-50 bg-green-600 text-white px-6 py-4 rounded-2xl shadow-2xl flex items-center space-x-3 border border-green-500 animate-bounce">
          <CheckCircle2 className="w-6 h-6 text-white" />
          <span className="font-medium text-sm">{toastMsg}</span>
        </div>
      )}

      {/* Error Banner */}
      {errorMsg && (
        <div className="bg-red-500/10 border border-red-500 text-red-600 dark:text-red-400 px-6 py-4 rounded-2xl mb-6 flex items-center space-x-3">
          <AlertCircle className="w-5 h-5 flex-shrink-0" />
          <span className="text-sm">{errorMsg}</span>
        </div>
      )}

      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 bg-white dark:bg-gray-800 p-6 rounded-3xl border border-slate-200 dark:border-gray-700 shadow-xl transition-colors duration-300">
        <div>
          <div className="flex items-center space-x-2 text-indigo-600 dark:text-indigo-400 text-xs font-bold uppercase tracking-wider mb-1">
            <Shield className="w-4 h-4" />
            <span>Panel de Administración</span>
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-800 dark:text-white">
            Configuración de Agenda y Disponibilidad
          </h1>
          <p className="text-slate-500 dark:text-gray-400 text-sm mt-1">
            Establezca las franjas horarias y duración de turnos para calcular automáticamente la disponibilidad de los clientes.
          </p>
        </div>
        <div className="mt-4 md:mt-0 flex items-center space-x-4">
          <button
            type="button"
            onClick={handleSave}
            disabled={loading}
            className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white px-6 py-3 rounded-2xl font-semibold shadow-lg shadow-indigo-600/30 transition-all disabled:opacity-50 cursor-pointer"
          >
            <Save className="w-5 h-5" />
            <span>{loading ? 'Guardando...' : 'Guardar Horarios'}</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Columna Izquierda: Configuración (Pasos 1, 2 y 3) */}
        <div className="lg:col-span-2 space-y-6">
          
          {/* PASO 1: Días de Atención */}
          <div className="bg-white dark:bg-gray-800 rounded-3xl p-6 border border-slate-200 dark:border-gray-700 shadow-lg transition-colors duration-300">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-bold flex items-center space-x-2 text-slate-800 dark:text-white">
                <Calendar className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
                <span>Paso 1: Seleccione Días de Atención</span>
              </h3>
              <button
                type="button"
                onClick={selectWeekdays}
                className="text-xs bg-indigo-50 dark:bg-indigo-500/20 text-indigo-600 dark:text-indigo-300 px-3 py-1.5 rounded-xl font-semibold hover:bg-indigo-100 dark:hover:bg-indigo-500/30 transition-all border border-indigo-200 dark:border-indigo-500/30 cursor-pointer"
              >
                Seleccionar Lu-Vi
              </button>
            </div>
            <p className="text-slate-500 dark:text-gray-400 text-sm mb-4">
              Haga clic sobre los días en los que el consultorio o profesional atenderá reservas.
            </p>

            <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-7 gap-3">
              {DAYS_MAP.map((day) => {
                const isSelected = selectedDays.includes(day.value);
                return (
                  <button
                    key={day.value}
                    type="button"
                    onClick={() => toggleDay(day.value)}
                    className={`flex flex-col items-center justify-center py-4 rounded-2xl border transition-all cursor-pointer ${
                      isSelected
                        ? 'bg-indigo-600 border-indigo-500 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-400/50'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:border-slate-300 hover:text-slate-900 dark:bg-gray-900 dark:border-gray-700 dark:text-gray-400 dark:hover:border-gray-600 dark:hover:text-white'
                    }`}
                  >
                    <span className="text-base font-bold">{day.label}</span>
                    <span className="mt-2 text-xs">
                      {isSelected ? <Check className="w-4 h-4 mx-auto" /> : <Plus className="w-4 h-4 mx-auto opacity-50" />}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* PASO 2: Jornada Horaria */}
          <div className="bg-white dark:bg-gray-800 rounded-3xl p-6 border border-slate-200 dark:border-gray-700 shadow-lg transition-colors duration-300">
            <h3 className="text-lg font-bold flex items-center space-x-2 mb-4 text-slate-800 dark:text-white">
              <Clock className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              <span>Paso 2: Rango de Horario de Atención</span>
            </h3>
            <p className="text-slate-500 dark:text-gray-400 text-sm mb-6">
              Defina la hora de apertura y de cierre para los días seleccionados.
            </p>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-gray-300 uppercase tracking-wider mb-2">Hora de Inicio</label>
                <input
                  type="time"
                  value={startTime}
                  onChange={(e) => setStartTime(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-gray-900 border border-slate-200 dark:border-gray-700 rounded-2xl px-4 py-3 text-slate-800 dark:text-white focus:outline-none focus:border-indigo-500 text-lg font-mono transition-colors duration-300"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-600 dark:text-gray-300 uppercase tracking-wider mb-2">Hora de Fin</label>
                <input
                  type="time"
                  value={endTime}
                  onChange={(e) => setEndTime(e.target.value)}
                  className="w-full bg-slate-50 dark:bg-gray-900 border border-slate-200 dark:border-gray-700 rounded-2xl px-4 py-3 text-slate-800 dark:text-white focus:outline-none focus:border-indigo-500 text-lg font-mono transition-colors duration-300"
                />
              </div>
            </div>
          </div>

          {/* PASO 3: Duración de Turnos (Slots) */}
          <div className="bg-white dark:bg-gray-800 rounded-3xl p-6 border border-slate-200 dark:border-gray-700 shadow-lg transition-colors duration-300">
            <h3 className="text-lg font-bold flex items-center space-x-2 mb-4 text-slate-800 dark:text-white">
              <Sparkles className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              <span>Paso 3: Duración de cada Turno (Slot)</span>
            </h3>
            <p className="text-slate-500 dark:text-gray-400 text-sm mb-6">
              Seleccione el intervalo de tiempo estándar asignado por cita.
            </p>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              {[15, 30, 45, 60].map((mins) => {
                const isSelected = slotDuration === mins;
                return (
                  <button
                    key={mins}
                    type="button"
                    onClick={() => setSlotDuration(mins)}
                    className={`py-4 px-4 rounded-2xl border text-center transition-all cursor-pointer ${
                      isSelected
                        ? 'bg-indigo-600 border-indigo-500 text-white shadow-lg shadow-indigo-600/30 ring-2 ring-indigo-400/50'
                        : 'bg-slate-50 border-slate-200 text-slate-600 hover:border-slate-300 hover:text-slate-900 dark:bg-gray-900 dark:border-gray-700 dark:text-gray-400 dark:hover:border-gray-600 dark:hover:text-white'
                    }`}
                  >
                    <span className="text-2xl font-black">{mins}</span>
                    <span className="block text-xs font-medium uppercase tracking-wider mt-1">Minutos</span>
                  </button>
                );
              })}
            </div>
          </div>

        </div>

        {/* Columna Derecha: Simulación e Insights */}
        <div className="space-y-6">
          
          {/* Métricas Estimadas */}
          <div className="bg-gradient-to-br from-indigo-50 to-slate-50 dark:from-indigo-900/50 dark:to-gray-800 rounded-3xl p-6 border border-indigo-200 dark:border-indigo-500/30 shadow-xl transition-colors duration-300">
            <h3 className="text-lg font-bold text-slate-800 dark:text-white mb-4 flex items-center space-x-2">
              <Sparkles className="w-5 h-5 text-indigo-600 dark:text-indigo-400" />
              <span>Simulación de Capacidad</span>
            </h3>

            <div className="grid grid-cols-2 gap-4 mb-6">
              <div className="bg-white dark:bg-gray-900/60 p-4 rounded-2xl border border-slate-200 dark:border-gray-700/50 text-center shadow-sm">
                <span className="block text-3xl font-black text-indigo-600 dark:text-indigo-400">{totalSlotsPerDay}</span>
                <span className="text-xs text-slate-500 dark:text-gray-400 font-medium uppercase tracking-wider">Turnos / Día</span>
              </div>
              <div className="bg-white dark:bg-gray-900/60 p-4 rounded-2xl border border-slate-200 dark:border-gray-700/50 text-center shadow-sm">
                <span className="block text-3xl font-black text-purple-600 dark:text-purple-400">{estimatedWeeklyCapacity}</span>
                <span className="text-xs text-slate-500 dark:text-gray-400 font-medium uppercase tracking-wider">Capacidad Semanal</span>
              </div>
            </div>

            <div className="text-xs text-slate-600 dark:text-gray-300 bg-indigo-500/10 p-4 rounded-2xl border border-indigo-500/20 space-y-1">
              <p className="font-semibold text-indigo-700 dark:text-indigo-300">💡 Algoritmo de Cálculo Dinámico:</p>
              <p>Los clientes visualizarán únicamente los slots teóricos calculados en este rango, descontando en tiempo real las citas ya reservadas o pasadas.</p>
            </div>
          </div>

          {/* Reglas Persistidas en Base de Datos */}
          <div className="bg-white dark:bg-gray-800 rounded-3xl p-6 border border-slate-200 dark:border-gray-700 shadow-xl transition-colors duration-300">
            <h3 className="text-lg font-bold text-slate-800 dark:text-white mb-4">Reglas Guardadas (PostgreSQL)</h3>

            {rules.length === 0 ? (
              <p className="text-slate-500 dark:text-gray-400 text-sm text-center py-6">No hay reglas de disponibilidad configuradas aún.</p>
            ) : (
              <div className="space-y-3 max-h-[320px] overflow-y-auto pr-1">
                {rules.map((rule) => (
                  <div key={rule.id} className="bg-slate-50 dark:bg-gray-900 p-4 rounded-2xl border border-slate-200 dark:border-gray-700/80 flex justify-between items-center transition-colors duration-300">
                    <div>
                      <span className="font-bold text-indigo-600 dark:text-indigo-400 text-sm">{rule.dayOfWeek}</span>
                      <div className="text-xs text-slate-600 dark:text-gray-300 mt-1 font-mono">
                        {rule.startTime.slice(0, 5)} - {rule.endTime.slice(0, 5)}
                      </div>
                    </div>
                    <span className="text-xs px-2.5 py-1 bg-white dark:bg-gray-800 text-slate-700 dark:text-gray-300 rounded-full border border-slate-200 dark:border-gray-700 font-medium shadow-sm">
                      {rule.slotDurationMinutes} min
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>

        </div>
      </div>
    </div>
  );
}
