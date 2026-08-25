import React, { useState } from 'react';
import { 
  DollarSign, CheckSquare, Zap, Clock, Shield, Plus, TrendingUp, 
  Layers, CreditCard, User, Heart, ArrowUpRight, ArrowDownLeft,
  Smartphone, Monitor, Bell, Settings, Search, Check, ChevronRight
} from 'lucide-react';

export const AppSimulator: React.FC = () => {
  const [viewMode, setViewMode] = useState<'desktop' | 'mobile'>('mobile');
  const [activeTab, setActiveTab] = useState<'cockpit' | 'finance' | 'tasks' | 'habits' | 'social'>('cockpit');
  
  // Estado interactivo financiero
  const [inPocket, setInPocket] = useState(485.50);
  const [healthScore] = useState(92);
  const [transactions, setTransactions] = useState([
    { id: '1', desc: 'Supermercado Central', amount: -65.20, category: 'Alimentación', date: 'Hoy', type: 'expense' },
    { id: '2', desc: 'Pago Nómina / Salario', amount: 2400.00, category: 'Salario', date: 'Ayer', type: 'income' },
    { id: '3', desc: 'Suscripción Netflix 4K', amount: -15.99, category: 'Suscripciones', date: '18 Ago', type: 'expense' },
    { id: '4', desc: 'Gasolina Estación Sur', amount: -35.00, category: 'Transporte', date: '17 Ago', type: 'expense' },
  ]);
  const [newDesc, setNewDesc] = useState('');
  const [newAmount, setNewAmount] = useState('');
  const [newType, setNewType] = useState<'expense' | 'income'>('expense');

  // Estado de tareas
  const [tasks, setTasks] = useState([
    { id: '1', title: 'Revisar informe de presupuesto mensual (YNAB)', done: false, priority: 'Alta', project: 'Finanzas' },
    { id: '2', title: 'Planificar sprint semanal (Motion AI)', done: true, priority: 'Media', project: 'Trabajo' },
    { id: '3', title: 'Comprar materiales para remodelación', done: false, priority: 'Baja', project: 'Hogar' },
    { id: '4', title: 'Respaldar base de datos en Supabase', done: true, priority: 'Alta', project: 'Dev' },
  ]);
  const [newTaskTitle, setNewTaskTitle] = useState('');

  // Estado de hábitos
  const [habits, setHabits] = useState([
    { id: '1', name: 'Lectura diaria (30 min)', streak: 14, completedToday: true, icon: '📖' },
    { id: '2', name: 'Entrenamiento & Gym', streak: 6, completedToday: false, icon: '💪' },
    { id: '3', name: 'Meditar 10 minutos', streak: 21, completedToday: true, icon: '🧘' },
    { id: '4', name: 'Anotar todos los gastos', streak: 9, completedToday: true, icon: '💰' },
  ]);

  // Pomodoro
  const [pomoMinutes, setPomoMinutes] = useState(25);
  const [pomoSeconds, setPomoSeconds] = useState(0);
  const [pomoActive, setPomoActive] = useState(false);

  const handleAddTransaction = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newDesc || !newAmount) return;
    const val = parseFloat(newAmount);
    const item = {
      id: Date.now().toString(),
      desc: newDesc,
      amount: newType === 'expense' ? -Math.abs(val) : Math.abs(val),
      category: newType === 'expense' ? 'Gasto General' : 'Ingreso Extra',
      date: 'Hoy',
      type: newType
    };
    setTransactions([item, ...transactions]);
    if (newType === 'expense') setInPocket(prev => Math.max(0, prev - val));
    else setInPocket(prev => prev + val);
    setNewDesc('');
    setNewAmount('');
  };

  const handleToggleTask = (id: string) => {
    setTasks(tasks.map(t => t.id === id ? { ...t, done: !t.done } : t));
  };

  const handleAddTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newTaskTitle) return;
    setTasks([...tasks, { id: Date.now().toString(), title: newTaskTitle, done: false, priority: 'Media', project: 'General' }]);
    setNewTaskTitle('');
  };

  const handleToggleHabit = (id: string) => {
    setHabits(habits.map(h => {
      if (h.id === id) {
        const next = !h.completedToday;
        return { ...h, completedToday: next, streak: next ? h.streak + 1 : Math.max(0, h.streak - 1) };
      }
      return h;
    }));
  };

  // RENDERIZADO DEL CUERPO DE LA APP
  const renderAppContent = () => (
    <div className="space-y-4">
      {/* COCKPIT DIARIO */}
      {activeTab === 'cockpit' && (
        <div className="space-y-4">
          {/* Card PocketGuard */}
          <div className="bg-gradient-to-br from-slate-900 to-indigo-950/80 border border-indigo-500/30 p-5 rounded-2xl relative overflow-hidden shadow-lg">
            <div className="flex justify-between items-center text-xs text-slate-400 mb-1">
              <span>DISPONIBLE PARA GASTAR</span>
              <span className="text-emerald-400 font-bold bg-emerald-500/10 px-2 py-0.5 rounded-full border border-emerald-500/20">PocketGuard</span>
            </div>
            <div className="text-3xl font-black text-white tracking-tight mb-2">
              ${inPocket.toFixed(2)}
            </div>
            <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden mb-2">
              <div className="bg-emerald-500 h-full rounded-full" style={{ width: '74%' }} />
            </div>
            <div className="flex justify-between text-[11px] text-slate-400">
              <span>Gastos fijos pagados</span>
              <span className="text-emerald-400 font-semibold">+15% de ahorro</span>
            </div>
          </div>

          {/* Salud Financiera Fintonic */}
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-xl bg-indigo-500/20 border border-indigo-500/30 flex items-center justify-center font-black text-indigo-400">
                {healthScore}
              </div>
              <div>
                <div className="text-xs font-bold text-white">Salud Financiera (Fintonic)</div>
                <div className="text-[11px] text-slate-400">Estado: <span className="text-emerald-400 font-semibold">Excelente</span></div>
              </div>
            </div>
            <span className="text-xs text-indigo-400 bg-indigo-500/10 border border-indigo-500/30 px-2.5 py-1 rounded-lg font-bold">Ver Score</span>
          </div>

          {/* Tareas de Hoy (Microsoft To Do + Motion) */}
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-3">
            <div className="flex justify-between items-center">
              <div className="text-xs font-bold text-white flex items-center space-x-1.5">
                <CheckSquare size={14} className="text-emerald-400" />
                <span>Mi Día & Tareas Hoy ({tasks.filter(t => t.done).length}/{tasks.length})</span>
              </div>
              <span className="text-[10px] text-slate-400 font-medium">Motion AI Auto</span>
            </div>

            <div className="space-y-2">
              {tasks.map(t => (
                <div 
                  key={t.id}
                  onClick={() => handleToggleTask(t.id)}
                  className={`p-2.5 rounded-xl border flex items-center justify-between cursor-pointer transition-all ${
                    t.done ? 'bg-slate-950/40 border-slate-800/60 opacity-60' : 'bg-slate-800/40 border-slate-700/60 hover:bg-slate-800/80'
                  }`}
                >
                  <div className="flex items-center space-x-2.5">
                    <div className={`w-4 h-4 rounded border flex items-center justify-center ${
                      t.done ? 'bg-emerald-500 border-emerald-500 text-white' : 'border-slate-600'
                    }`}>
                      {t.done && <Check size={12} />}
                    </div>
                    <span className={`text-xs ${t.done ? 'line-through text-slate-500' : 'text-slate-200'}`}>{t.title}</span>
                  </div>
                  <span className="text-[10px] px-1.5 py-0.5 rounded bg-slate-800 text-slate-400 border border-slate-700">{t.project}</span>
                </div>
              ))}
            </div>

            <form onSubmit={handleAddTask} className="flex gap-1.5 pt-1">
              <input 
                type="text" 
                placeholder="Agregar tarea rápida..." 
                value={newTaskTitle}
                onChange={e => setNewTaskTitle(e.target.value)}
                className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500"
              />
              <button type="submit" className="bg-indigo-600 hover:bg-indigo-500 text-white px-3 py-2 rounded-xl text-xs font-bold">
                <Plus size={14} />
              </button>
            </form>
          </div>

          {/* Registro 1-tap de gastos (Monefy) */}
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-3">
            <div className="text-xs font-bold text-white flex items-center justify-between">
              <span>Registro Rápido (Monefy)</span>
              <span className="text-[10px] text-slate-400">1-Tap Input</span>
            </div>
            <form onSubmit={handleAddTransaction} className="grid grid-cols-12 gap-1.5">
              <input 
                type="text" 
                placeholder="Gasto / Ingreso..." 
                value={newDesc}
                onChange={e => setNewDesc(e.target.value)}
                className="col-span-6 bg-slate-950 border border-slate-800 rounded-xl px-2.5 py-2 text-xs text-white placeholder-slate-500"
              />
              <input 
                type="number" 
                placeholder="$ Monto" 
                value={newAmount}
                onChange={e => setNewAmount(e.target.value)}
                className="col-span-3 bg-slate-950 border border-slate-800 rounded-xl px-2.5 py-2 text-xs text-white placeholder-slate-500"
              />
              <button type="submit" className="col-span-3 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl text-xs font-bold">
                + Guardar
              </button>
            </form>
          </div>
        </div>
      )}

      {/* PESTAÑA FINANZAS */}
      {activeTab === 'finance' && (
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div className="bg-slate-900 border border-slate-800 p-3.5 rounded-2xl">
              <div className="text-[10px] text-slate-400">Patrimonio (Empower)</div>
              <div className="text-lg font-black text-white">$42,850.00</div>
              <div className="text-[10px] text-emerald-400 font-semibold">+4.2% mes</div>
            </div>
            <div className="bg-slate-900 border border-slate-800 p-3.5 rounded-2xl">
              <div className="text-[10px] text-slate-400">Presupuesto (YNAB)</div>
              <div className="text-lg font-black text-indigo-400">$3,200.00</div>
              <div className="text-[10px] text-slate-400">Zero-Based</div>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-2.5">
            <div className="text-xs font-bold text-white">Subcuentas Virtuales (N26 / Revolut)</div>
            <div className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center">
              <div>
                <div className="text-xs font-bold text-slate-200">Checking Principal</div>
                <div className="text-[10px] text-slate-400">Conexión Bancaria Activa</div>
              </div>
              <div className="text-sm font-black text-white">$2,840.50</div>
            </div>
            <div className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center">
              <div>
                <div className="text-xs font-bold text-emerald-400">Fondo de Emergencia (Meta)</div>
                <div className="text-[10px] text-slate-400">65% de la meta ($10,000)</div>
              </div>
              <div className="text-sm font-black text-emerald-400">$6,500.00</div>
            </div>
            <div className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center">
              <div>
                <div className="text-xs font-bold text-pink-400">Hogar Compartido (Honeydue)</div>
                <div className="text-[10px] text-slate-400">Billetera Pareja / Familia</div>
              </div>
              <div className="text-sm font-black text-pink-400">$1,450.00</div>
            </div>
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-2">
            <div className="text-xs font-bold text-white">Últimas Transacciones</div>
            {transactions.slice(0, 4).map(tx => (
              <div key={tx.id} className="p-2.5 bg-slate-800/30 border border-slate-700/50 rounded-xl flex justify-between items-center text-xs">
                <div className="flex items-center space-x-2">
                  <div className={`w-6 h-6 rounded flex items-center justify-center text-xs ${tx.amount > 0 ? 'bg-emerald-500/20 text-emerald-400' : 'bg-red-500/20 text-red-400'}`}>
                    {tx.amount > 0 ? '+' : '-'}
                  </div>
                  <div>
                    <div className="font-semibold text-slate-200">{tx.desc}</div>
                    <div className="text-[10px] text-slate-500">{tx.category}</div>
                  </div>
                </div>
                <div className={`font-bold ${tx.amount > 0 ? 'text-emerald-400' : 'text-slate-200'}`}>
                  {tx.amount > 0 ? `+$${tx.amount.toFixed(2)}` : `-$${Math.abs(tx.amount).toFixed(2)}`}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* PESTAÑA TAREAS */}
      {activeTab === 'tasks' && (
        <div className="space-y-4">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-3">
            <div className="text-xs font-bold text-white flex justify-between">
              <span>Metodología GTD (OmniFocus / ClickUp)</span>
              <span className="text-[10px] text-indigo-400">Kanban Activo</span>
            </div>

            <div className="space-y-2">
              {tasks.map(t => (
                <div key={t.id} className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center text-xs">
                  <div>
                    <div className="font-bold text-slate-200">{t.title}</div>
                    <div className="text-[10px] text-slate-400">{t.project} • Prioridad {t.priority}</div>
                  </div>
                  <button onClick={() => handleToggleTask(t.id)} className={`px-2 py-1 rounded text-[10px] font-bold ${t.done ? 'bg-slate-700 text-slate-400' : 'bg-indigo-600 text-white'}`}>
                    {t.done ? 'Hecho' : 'Completar'}
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* PESTAÑA HÁBITOS & POMODORO */}
      {activeTab === 'habits' && (
        <div className="space-y-4">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-3">
            <div className="text-xs font-bold text-white">Rastreador de Hábitos (TickTick)</div>
            {habits.map(h => (
              <div key={h.id} className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center">
                <div className="flex items-center space-x-2.5">
                  <span className="text-xl">{h.icon}</span>
                  <div>
                    <div className="text-xs font-bold text-white">{h.name}</div>
                    <div className="text-[10px] text-amber-400">🔥 Racha: {h.streak} días</div>
                  </div>
                </div>
                <button 
                  onClick={() => handleToggleHabit(h.id)}
                  className={`px-2.5 py-1 rounded-lg text-xs font-bold ${h.completedToday ? 'bg-emerald-500 text-white' : 'bg-slate-700 text-slate-300'}`}
                >
                  {h.completedToday ? '✓ Listo' : 'Marcar'}
                </button>
              </div>
            ))}
          </div>

          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl text-center space-y-2">
            <div className="text-xs font-bold text-white">Temporizador Pomodoro</div>
            <div className="text-3xl font-black text-indigo-400 py-2">
              {String(pomoMinutes).padStart(2, '0')}:{String(pomoSeconds).padStart(2, '0')}
            </div>
            <button 
              onClick={() => setPomoActive(!pomoActive)}
              className="bg-indigo-600 text-white text-xs font-bold px-4 py-2 rounded-xl"
            >
              {pomoActive ? 'Pausar' : 'Iniciar 25 min'}
            </button>
          </div>
        </div>
      )}

      {/* PESTAÑA SOCIAL & SOPORTE */}
      {activeTab === 'social' && (
        <div className="space-y-4">
          <div className="bg-slate-900 border border-slate-800 p-4 rounded-2xl space-y-3">
            <div className="text-xs font-bold text-white">Comunidad & Soporte Técnico</div>
            <div className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl">
              <div className="text-xs font-bold text-indigo-300">Grupo: Finanzas & Libertad Financiera</div>
              <div className="text-[10px] text-slate-400 mt-0.5">342 miembros activos compartiendo metas.</div>
            </div>
            <div className="p-3 bg-slate-800/40 border border-slate-700/60 rounded-xl flex justify-between items-center">
              <div>
                <div className="text-xs font-bold text-emerald-400">Ticket de Soporte Activo #104</div>
                <div className="text-[10px] text-slate-400">Estado: En revisión por equipo técnico</div>
              </div>
              <span className="text-[10px] bg-emerald-500/20 text-emerald-400 px-2 py-0.5 rounded font-bold">Abierto</span>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      {/* Barra de control superior para alternar vista */}
      <div className="flex items-center justify-between bg-slate-900/90 border border-slate-800 p-4 rounded-2xl">
        <div>
          <h2 className="text-base font-bold text-white">Visualizador Interactivo de KoreLife</h2>
          <p className="text-xs text-slate-400">Prueba todas las funciones en vivo directamente en tu navegador.</p>
        </div>
        <div className="flex items-center space-x-2 bg-slate-950 p-1 rounded-xl border border-slate-800">
          <button 
            onClick={() => setViewMode('mobile')}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              viewMode === 'mobile' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            <Smartphone size={14} />
            <span>Vista Móvil (Smartphone)</span>
          </button>
          <button 
            onClick={() => setViewMode('desktop')}
            className={`flex items-center space-x-1.5 px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              viewMode === 'desktop' ? 'bg-indigo-600 text-white' : 'text-slate-400 hover:text-white'
            }`}
          >
            <Monitor size={14} />
            <span>Vista Pantalla Completa</span>
          </button>
        </div>
      </div>

      {/* MODO SMARTPHONE SIMULADO */}
      {viewMode === 'mobile' ? (
        <div className="flex justify-center py-4">
          <div className="w-[390px] h-[810px] bg-slate-950 rounded-[48px] border-[10px] border-slate-800 shadow-2xl shadow-indigo-950/40 flex flex-col overflow-hidden relative">
            {/* Notch / Dynamic Island */}
            <div className="w-full pt-3 pb-2 flex justify-center items-center bg-slate-950 z-20">
              <div className="w-24 h-4 bg-slate-800 rounded-full" />
            </div>

            {/* Header del móvil */}
            <div className="px-5 py-2 flex items-center justify-between border-b border-slate-800/80 bg-slate-950/90 backdrop-blur z-10">
              <div className="flex items-center space-x-2">
                <div className="w-7 h-7 rounded-lg bg-emerald-500/20 border border-emerald-500/40 flex items-center justify-center font-black text-emerald-400 text-xs">
                  K
                </div>
                <span className="font-black text-white text-sm">KoreLife</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                <span className="text-[10px] text-slate-400 font-bold">Cloud Sync</span>
              </div>
            </div>

            {/* Contenido scrolleable de la app */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {renderAppContent()}
            </div>

            {/* Tab Bar inferior del móvil */}
            <div className="h-16 bg-slate-900 border-t border-slate-800 flex items-center justify-around px-2 z-20">
              {[
                { id: 'cockpit', icon: Zap, label: 'Inicio' },
                { id: 'finance', icon: DollarSign, label: 'Finanzas' },
                { id: 'tasks', icon: CheckSquare, label: 'Tareas' },
                { id: 'habits', icon: Clock, label: 'Hábitos' },
                { id: 'social', icon: User, label: 'Comunidad' },
              ].map(tab => {
                const Icon = tab.icon;
                const isActive = activeTab === tab.id;
                return (
                  <button 
                    key={tab.id}
                    onClick={() => setActiveTab(tab.id as any)}
                    className={`flex flex-col items-center justify-center flex-1 py-1 transition-all ${
                      isActive ? 'text-emerald-400' : 'text-slate-500 hover:text-slate-300'
                    }`}
                  >
                    <Icon size={18} />
                    <span className="text-[10px] font-bold mt-0.5">{tab.label}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      ) : (
        /* MODO DESKTOP EXPANDIDO */
        <div className="bg-slate-900/50 border border-slate-800 rounded-3xl p-6">
          <div className="flex space-x-2 border-b border-slate-800 pb-4 mb-6">
            {[
              { id: 'cockpit', icon: Zap, label: 'Daily Cockpit' },
              { id: 'finance', icon: DollarSign, label: 'Módulo Financiero' },
              { id: 'tasks', icon: CheckSquare, label: 'Tareas & GTD' },
              { id: 'habits', icon: Clock, label: 'Hábitos & Pomodoro' },
              { id: 'social', icon: User, label: 'Comunidad & Soporte' },
            ].map(tab => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center space-x-2 px-4 py-2.5 rounded-xl text-xs font-bold transition-all ${
                    isActive ? 'bg-indigo-600 text-white shadow-lg' : 'bg-slate-800/60 text-slate-300 hover:bg-slate-800'
                  }`}
                >
                  <Icon size={16} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>
          {renderAppContent()}
        </div>
      )}
    </div>
  );
};
