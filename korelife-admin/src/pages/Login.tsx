import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { Button } from '../components/ui/Button';
import { User } from '../types';
import { ShieldCheck, Lock, Mail, Sparkles } from 'lucide-react';
import toast from 'react-hot-toast';

export const Login: React.FC = () => {
  const [email, setEmail] = useState('admin@korelife.com');
  const [password, setPassword] = useState('admin123');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);

  const handleLogin = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    
    // Permitir acceso de administrador
    const mockUser: User = {
      id: 'admin_master',
      email: email || 'admin@korelife.com',
      full_name: 'Eduardo José (Admin)',
      avatar_url: null,
      role: 'superadmin',
      plan: 'team',
      status: 'active',
      created_at: new Date().toISOString()
    };

    login(mockUser);
    toast.success('Sesión iniciada como Superadministrador');
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-dark-bg flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-dark-card p-8 rounded-2xl shadow-2xl border border-dark-border">
        <div className="text-center mb-8">
          <div className="w-14 h-14 mx-auto mb-4 rounded-2xl bg-gradient-to-tr from-primary-600 to-emerald-500 flex items-center justify-center text-white text-2xl font-bold shadow-lg shadow-primary-500/20">
            K
          </div>
          <h1 className="text-2xl font-bold text-white mb-1">KoreLife Admin Portal</h1>
          <p className="text-xs text-dark-muted">Centro de Control & Administración Global</p>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-500/10 border border-red-500 text-red-400 rounded-lg text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5 flex items-center gap-1.5">
              <Mail size={14} className="text-primary-400" /> Correo Electrónico
            </label>
            <input
              type="email"
              required
              className="w-full px-4 py-2.5 bg-dark-bg border border-dark-border rounded-xl focus:border-primary-500 text-white text-sm outline-none transition-colors"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="admin@korelife.com"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5 flex items-center gap-1.5">
              <Lock size={14} className="text-primary-400" /> Contraseña de Acceso
            </label>
            <input
              type="password"
              required
              className="w-full px-4 py-2.5 bg-dark-bg border border-dark-border rounded-xl focus:border-primary-500 text-white text-sm outline-none transition-colors"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
            />
          </div>

          <Button type="submit" className="w-full py-3 rounded-xl font-semibold shadow-lg shadow-primary-600/30 mt-2">
            Iniciar Sesión
          </Button>
        </form>

        <div className="mt-6 pt-6 border-t border-dark-border text-center">
          <p className="text-xs text-dark-muted mb-3">Acceso directo para evaluación y soporte:</p>
          <Button
            type="button"
            variant="secondary"
            className="w-full py-2.5 rounded-xl text-xs flex items-center justify-center gap-2 border-emerald-500/30 text-emerald-400 hover:bg-emerald-950/20"
            onClick={() => handleLogin()}
          >
            <Sparkles size={14} /> Acceso Rápido Superadministrador
          </Button>
        </div>
      </div>
    </div>
  );
};
