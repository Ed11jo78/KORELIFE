import React from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import {
  Home,
  Users,
  CreditCard,
  Globe,
  Ticket,
  HelpCircle,
  Megaphone,
  BarChart2,
  Smartphone,
  LogOut,
  ShieldCheck
} from 'lucide-react';

export const Layout: React.FC = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItems = [
    { path: '/', icon: Home, label: 'Dashboard' },
    { path: '/users', icon: Users, label: 'Usuarios' },
    { path: '/transactions', icon: CreditCard, label: 'Transacciones Globales' },
    { path: '/rates', icon: Globe, label: 'Control de Tasas' },
    { path: '/tickets', icon: Ticket, label: 'Mesa de Soporte' },
    { path: '/announcements', icon: Megaphone, label: 'Anuncios Globales' },
    { path: '/faq', icon: HelpCircle, label: 'Centro FAQ' },
    { path: '/analytics', icon: BarChart2, label: 'Métricas & KPIs' },
    { path: '/simulator', icon: Smartphone, label: 'Simulador App Móvil' },
  ];

  return (
    <div className="flex h-screen bg-dark-bg text-dark-text">
      {/* Sidebar */}
      <aside className="w-64 bg-dark-card border-r border-dark-border flex flex-col">
        <div className="p-5 flex items-center justify-between border-b border-dark-border">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-primary-600 to-emerald-500 flex items-center justify-center text-white font-bold text-base shadow-md">
              K
            </div>
            <div>
              <h1 className="text-base font-bold text-white leading-tight">KoreLife</h1>
              <p className="text-[10px] text-primary-400 font-semibold tracking-wider uppercase">Portal Admin</p>
            </div>
          </div>
          <span className="flex h-2 w-2 relative">
            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
          </span>
        </div>

        <nav className="flex-1 p-3 space-y-1 overflow-y-auto">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-xs font-medium transition-all ${
                  isActive
                    ? 'bg-primary-600/20 text-primary-400 border border-primary-500/30 shadow-sm'
                    : 'text-dark-muted hover:bg-dark-bg hover:text-slate-200'
                }`}
              >
                <item.icon size={18} className={isActive ? 'text-primary-400' : 'text-dark-muted'} />
                {item.label}
              </Link>
            );
          })}
        </nav>

        {/* User Card & Logout */}
        <div className="p-3 border-t border-dark-border bg-dark-bg/50">
          <div className="flex items-center gap-3 mb-3 p-2 rounded-lg bg-dark-card border border-dark-border">
            <div className="w-8 h-8 rounded-full bg-primary-600 flex items-center justify-center text-white text-xs font-bold shadow">
              {user?.full_name?.[0] || 'A'}
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-white truncate">{user?.full_name || 'Admin Master'}</p>
              <p className="text-[10px] text-emerald-400 flex items-center gap-1 font-medium">
                <ShieldCheck size={12} /> Superadministrador
              </p>
            </div>
          </div>

          <button
            onClick={handleLogout}
            className="flex items-center justify-center gap-2 text-rose-400 hover:text-rose-300 w-full px-3 py-2 text-xs font-medium transition-colors rounded-lg hover:bg-rose-950/30 border border-rose-500/20"
          >
            <LogOut size={14} /> Cerrar Sesión
          </button>
        </div>
      </aside>

      {/* Main Area */}
      <main className="flex-1 overflow-auto flex flex-col">
        <header className="h-16 bg-dark-card border-b border-dark-border flex items-center justify-between px-8 shrink-0">
          <div>
            <h2 className="text-lg font-bold text-white">
              {location.pathname === '/' ? 'Dashboard Principal' :
               location.pathname === '/users' ? 'Directorio de Usuarios' :
               location.pathname === '/transactions' ? 'Auditoría de Finanzas' :
               location.pathname === '/rates' ? 'Control de Tasas de Cambio' :
               location.pathname === '/tickets' ? 'Mesa de Ayuda y Soporte' :
               location.pathname === '/announcements' ? 'Anuncios y Notificaciones' :
               location.pathname === '/faq' ? 'Centro de Preguntas Frecuentes' :
               location.pathname === '/analytics' ? 'Métricas de la Plataforma' :
               location.pathname === '/simulator' ? 'Simulador de App Móvil' : 'Panel de Control'}
            </h2>
          </div>

          <div className="flex items-center gap-3">
            <span className="text-xs bg-dark-bg border border-dark-border px-3 py-1.5 rounded-full text-dark-muted font-mono">
              v1.2.0 • Supabase Live
            </span>
          </div>
        </header>

        <div className="p-8 flex-1">
          <Outlet />
        </div>
      </main>
    </div>
  );
};
