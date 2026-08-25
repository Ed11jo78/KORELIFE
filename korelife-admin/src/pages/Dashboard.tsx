import React from 'react';
import { Users, CreditCard, Ticket, DollarSign, Wallet, ArrowUpRight, ShieldCheck, Globe, Activity } from 'lucide-react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { Link } from 'react-router-dom';

const growthData = [
  { month: 'Mar', users: 120, volume: 15000 },
  { month: 'Abr', users: 240, volume: 28000 },
  { month: 'May', users: 450, volume: 54000 },
  { month: 'Jun', users: 780, volume: 92000 },
  { month: 'Jul', users: 1200, volume: 145000 },
  { month: 'Ago', users: 1850, volume: 232000 },
];

export const Dashboard: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-primary-950 via-dark-card to-dark-card border border-primary-500/30 p-6 rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <span className="text-xs font-semibold px-2.5 py-1 rounded bg-primary-500/20 text-primary-400 border border-primary-500/30">
            PANEL DE CONTROL GENERAL
          </span>
          <h2 className="text-2xl font-bold text-white mt-2">Bienvenido al Centro de Mando KoreLife</h2>
          <p className="text-xs text-dark-muted mt-1">
            Supervisa en tiempo real usuarios, transacciones multimoneda, estado de servidores y soporte.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="px-4 py-2 bg-dark-bg/80 border border-dark-border rounded-xl flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
            <span className="text-xs text-slate-200 font-medium">PostgreSQL: <strong className="text-emerald-400">En Línea</strong></span>
          </div>
          <Link
            to="/users"
            className="px-4 py-2 bg-primary-600 hover:bg-primary-500 text-white rounded-xl text-xs font-semibold transition-all shadow-md"
          >
            + Gestionar Usuarios
          </Link>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
          <div className="flex items-center justify-between">
            <span className="text-xs text-dark-muted font-medium">Usuarios Registrados</span>
            <div className="p-2.5 bg-blue-500/20 text-blue-400 rounded-lg">
              <Users size={20} />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">1,850</p>
          <div className="flex items-center gap-1 text-xs text-emerald-400 mt-2 font-medium">
            <ArrowUpRight size={14} /> +18.4% este mes
          </div>
        </div>

        <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
          <div className="flex items-center justify-between">
            <span className="text-xs text-dark-muted font-medium">Membresías Pro / Team</span>
            <div className="p-2.5 bg-emerald-500/20 text-emerald-400 rounded-lg">
              <CreditCard size={20} />
            </div>
          </div>
          <p className="text-2xl font-bold text-emerald-400 mt-2">485</p>
          <div className="flex items-center gap-1 text-xs text-emerald-400 mt-2 font-medium">
            <ArrowUpRight size={14} /> 26.2% tasa de conversión
          </div>
        </div>

        <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
          <div className="flex items-center justify-between">
            <span className="text-xs text-dark-muted font-medium">Volumen Transaccionado</span>
            <div className="p-2.5 bg-purple-500/20 text-purple-400 rounded-lg">
              <Wallet size={20} />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2 font-mono">$232.5K <span className="text-xs text-dark-muted font-normal">USD</span></p>
          <p className="text-xs text-dark-muted mt-2 font-mono">≈ 11.3M Bolívares (VES)</p>
        </div>

        <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
          <div className="flex items-center justify-between">
            <span className="text-xs text-dark-muted font-medium">Tickets de Soporte</span>
            <div className="p-2.5 bg-amber-500/20 text-amber-400 rounded-lg">
              <Ticket size={20} />
            </div>
          </div>
          <p className="text-2xl font-bold text-white mt-2">6 <span className="text-xs text-amber-400 font-normal">pendientes</span></p>
          <div className="flex items-center gap-1 text-xs text-emerald-400 mt-2 font-medium">
            ✓ 98.4% resueltos en &lt; 1h
          </div>
        </div>
      </div>

      {/* Chart & Live Status */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Growth Chart */}
        <div className="lg:col-span-2 bg-dark-card p-6 rounded-xl border border-dark-border">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h3 className="text-base font-semibold text-white">Crecimiento de Usuarios & Volumen</h3>
              <p className="text-xs text-dark-muted">Historial de adopción de la plataforma en 2026</p>
            </div>
            <span className="text-xs bg-dark-bg px-2.5 py-1 rounded border border-dark-border text-primary-400 font-medium">
              Semestral
            </span>
          </div>

          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={growthData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1e293b" vertical={false} />
                <XAxis dataKey="month" stroke="#64748b" axisLine={false} tickLine={false} />
                <YAxis stroke="#64748b" axisLine={false} tickLine={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #334155', borderRadius: '0.75rem', color: '#fff' }}
                />
                <Line type="monotone" dataKey="users" stroke="#6366f1" strokeWidth={3} dot={{ fill: '#6366f1', r: 4 }} name="Usuarios" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* System Health & Quick Links */}
        <div className="space-y-4">
          <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
            <h3 className="text-sm font-semibold text-white mb-3 flex items-center gap-2">
              <Activity size={16} className="text-emerald-400" /> Estado de la Infraestructura
            </h3>

            <div className="space-y-3 text-xs">
              <div className="flex justify-between items-center p-2.5 bg-dark-bg rounded-lg border border-dark-border">
                <span className="text-slate-300">Base de Datos Supabase</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">● 99.98% SLA</span>
              </div>
              <div className="flex justify-between items-center p-2.5 bg-dark-bg rounded-lg border border-dark-border">
                <span className="text-slate-300">Web App (Vercel Edge)</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">● 42ms Latencia</span>
              </div>
              <div className="flex justify-between items-center p-2.5 bg-dark-bg rounded-lg border border-dark-border">
                <span className="text-slate-300">Scraper de Tasas BCV/Binance</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">● Sincronizado</span>
              </div>
              <div className="flex justify-between items-center p-2.5 bg-dark-bg rounded-lg border border-dark-border">
                <span className="text-slate-300">Notificaciones Push (EAS)</span>
                <span className="text-emerald-400 font-semibold flex items-center gap-1">● Activas</span>
              </div>
            </div>
          </div>

          <div className="bg-dark-card p-5 rounded-xl border border-dark-border">
            <h3 className="text-sm font-semibold text-white mb-3">Accesos Directos del Staff</h3>
            <div className="grid grid-cols-2 gap-2">
              <Link to="/transactions" className="p-3 bg-dark-bg hover:bg-dark-border/40 rounded-lg border border-dark-border text-center text-xs font-medium text-slate-200 transition-colors">
                💳 Ver Transacciones
              </Link>
              <Link to="/rates" className="p-3 bg-dark-bg hover:bg-dark-border/40 rounded-lg border border-dark-border text-center text-xs font-medium text-slate-200 transition-colors">
                🌐 Ajustar Tasas
              </Link>
              <Link to="/tickets" className="p-3 bg-dark-bg hover:bg-dark-border/40 rounded-lg border border-dark-border text-center text-xs font-medium text-slate-200 transition-colors">
                🎫 Responder Tickets
              </Link>
              <Link to="/announcements" className="p-3 bg-dark-bg hover:bg-dark-border/40 rounded-lg border border-dark-border text-center text-xs font-medium text-slate-200 transition-colors">
                📢 Enviar Noticia
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
