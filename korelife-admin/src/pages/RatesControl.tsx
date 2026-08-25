import React, { useState } from 'react';
import { Button } from '../components/ui/Button';
import { RefreshCw, CheckCircle, TrendingUp, DollarSign, Globe, Shield, Save } from 'lucide-react';
import toast from 'react-hot-toast';

export const RatesControl: React.FC = () => {
  const [bcvUsd, setBcvUsd] = useState(41.85);
  const [bcvEur, setBcvEur] = useState(45.20);
  const [binanceUsdt, setBinanceUsdt] = useState(48.90);
  const [airtmUsd, setAirtmUsd] = useState(49.10);
  const [activeRate, setActiveRate] = useState<'bcv' | 'binance' | 'airtm'>('bcv');
  const [isUpdating, setIsUpdating] = useState(false);
  const [lastSync, setLastSync] = useState(new Date().toLocaleTimeString());

  const handleSyncScraper = () => {
    setIsUpdating(true);
    setTimeout(() => {
      setIsUpdating(false);
      setLastSync(new Date().toLocaleTimeString());
      toast.success('Tasas de cambio actualizadas con éxito desde fuentes oficiales');
    }, 1200);
  };

  const handleSaveOverrides = () => {
    toast.success('Tasas del sistema guardadas y propagadas a la red de usuarios');
  };

  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-primary-900/60 to-dark-card border border-primary-500/30 p-6 rounded-2xl flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <div className="flex items-center gap-2 text-primary-400 font-semibold text-sm">
            <Globe size={18} />
            <span>Motor de Tasas Multimoneda</span>
          </div>
          <h2 className="text-2xl font-bold text-white mt-1">Control y Monitoreo de Tipos de Cambio</h2>
          <p className="text-xs text-dark-muted mt-1">
            Última sincronización: <strong className="text-slate-300">{lastSync}</strong> | Estado del Scraper: <span className="text-emerald-400 font-semibold">● Operacional</span>
          </p>
        </div>

        <div className="flex gap-3">
          <Button
            variant="secondary"
            className="flex items-center gap-2"
            onClick={handleSyncScraper}
            disabled={isUpdating}
          >
            <RefreshCw size={16} className={isUpdating ? 'animate-spin' : ''} />
            {isUpdating ? 'Sincronizando...' : 'Forzar Sincronización'}
          </Button>

          <Button
            variant="primary"
            className="flex items-center gap-2"
            onClick={handleSaveOverrides}
          >
            <Save size={16} />
            Guardar Cambios
          </Button>
        </div>
      </div>

      {/* Grid of Exchange Rates */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* BCV USD */}
        <div className={`p-5 rounded-xl border transition-all ${
          activeRate === 'bcv' ? 'bg-primary-950/40 border-primary-500 shadow-lg shadow-primary-500/10' : 'bg-dark-card border-dark-border'
        }`}>
          <div className="flex justify-between items-start">
            <div>
              <span className="text-xs font-semibold px-2 py-0.5 rounded bg-blue-500/20 text-blue-400 border border-blue-500/30">
                OFICIAL BCV
              </span>
              <h3 className="text-lg font-bold text-white mt-2">Dólar BCV (USD)</h3>
            </div>
            <div className="w-9 h-9 rounded-full bg-blue-600/20 flex items-center justify-center text-blue-400 font-bold">
              $
            </div>
          </div>
          <div className="mt-4">
            <label className="text-xs text-dark-muted">Tasa en Bs. / USD</label>
            <input
              type="number"
              step="0.01"
              value={bcvUsd}
              onChange={(e) => setBcvUsd(parseFloat(e.target.value) || 0)}
              className="w-full mt-1 bg-dark-bg border border-dark-border px-3 py-2 rounded-lg font-mono font-bold text-lg text-white outline-none focus:border-primary-500"
            />
          </div>
          <div className="mt-4 flex justify-between items-center text-xs">
            <span className="text-emerald-400 flex items-center gap-1 font-medium"><TrendingUp size={14} /> +0.25% hoy</span>
            <button
              onClick={() => { setActiveRate('bcv'); toast.success('Tasa predeterminada cambiada a BCV USD'); }}
              className={`px-2.5 py-1 rounded text-xs font-medium ${
                activeRate === 'bcv' ? 'bg-primary-600 text-white' : 'bg-dark-bg text-dark-muted hover:text-white'
              }`}
            >
              {activeRate === 'bcv' ? '✓ Activa' : 'Fijar Activa'}
            </button>
          </div>
        </div>

        {/* BCV EUR */}
        <div className="p-5 rounded-xl bg-dark-card border border-dark-border">
          <div className="flex justify-between items-start">
            <div>
              <span className="text-xs font-semibold px-2 py-0.5 rounded bg-indigo-500/20 text-indigo-400 border border-indigo-500/30">
                OFICIAL BCV
              </span>
              <h3 className="text-lg font-bold text-white mt-2">Euro BCV (EUR)</h3>
            </div>
            <div className="w-9 h-9 rounded-full bg-indigo-600/20 flex items-center justify-center text-indigo-400 font-bold">
              €
            </div>
          </div>
          <div className="mt-4">
            <label className="text-xs text-dark-muted">Tasa en Bs. / EUR</label>
            <input
              type="number"
              step="0.01"
              value={bcvEur}
              onChange={(e) => setBcvEur(parseFloat(e.target.value) || 0)}
              className="w-full mt-1 bg-dark-bg border border-dark-border px-3 py-2 rounded-lg font-mono font-bold text-lg text-white outline-none focus:border-primary-500"
            />
          </div>
          <div className="mt-4 flex justify-between items-center text-xs">
            <span className="text-emerald-400 flex items-center gap-1 font-medium"><TrendingUp size={14} /> +0.18% hoy</span>
            <span className="text-dark-muted">Referencia Oficial</span>
          </div>
        </div>

        {/* Binance P2P */}
        <div className={`p-5 rounded-xl border transition-all ${
          activeRate === 'binance' ? 'bg-primary-950/40 border-primary-500 shadow-lg shadow-primary-500/10' : 'bg-dark-card border-dark-border'
        }`}>
          <div className="flex justify-between items-start">
            <div>
              <span className="text-xs font-semibold px-2 py-0.5 rounded bg-amber-500/20 text-amber-400 border border-amber-500/30">
                MERCADO P2P
              </span>
              <h3 className="text-lg font-bold text-white mt-2">Binance P2P (USDT)</h3>
            </div>
            <div className="w-9 h-9 rounded-full bg-amber-500/20 flex items-center justify-center text-amber-400 font-bold">
              ₮
            </div>
          </div>
          <div className="mt-4">
            <label className="text-xs text-dark-muted">Tasa en Bs. / USDT</label>
            <input
              type="number"
              step="0.01"
              value={binanceUsdt}
              onChange={(e) => setBinanceUsdt(parseFloat(e.target.value) || 0)}
              className="w-full mt-1 bg-dark-bg border border-dark-border px-3 py-2 rounded-lg font-mono font-bold text-lg text-white outline-none focus:border-primary-500"
            />
          </div>
          <div className="mt-4 flex justify-between items-center text-xs">
            <span className="text-emerald-400 flex items-center gap-1 font-medium"><TrendingUp size={14} /> Tiempo Real</span>
            <button
              onClick={() => { setActiveRate('binance'); toast.success('Tasa predeterminada cambiada a Binance USDT'); }}
              className={`px-2.5 py-1 rounded text-xs font-medium ${
                activeRate === 'binance' ? 'bg-primary-600 text-white' : 'bg-dark-bg text-dark-muted hover:text-white'
              }`}
            >
              {activeRate === 'binance' ? '✓ Activa' : 'Fijar Activa'}
            </button>
          </div>
        </div>

        {/* Airtm */}
        <div className={`p-5 rounded-xl border transition-all ${
          activeRate === 'airtm' ? 'bg-primary-950/40 border-primary-500 shadow-lg shadow-primary-500/10' : 'bg-dark-card border-dark-border'
        }`}>
          <div className="flex justify-between items-start">
            <div>
              <span className="text-xs font-semibold px-2 py-0.5 rounded bg-cyan-500/20 text-cyan-400 border border-cyan-500/30">
                P2P AIRTM
              </span>
              <h3 className="text-lg font-bold text-white mt-2">Airtm (AirUSD)</h3>
            </div>
            <div className="w-9 h-9 rounded-full bg-cyan-500/20 flex items-center justify-center text-cyan-400 font-bold">
              A
            </div>
          </div>
          <div className="mt-4">
            <label className="text-xs text-dark-muted">Tasa en Bs. / AirUSD</label>
            <input
              type="number"
              step="0.01"
              value={airtmUsd}
              onChange={(e) => setAirtmUsd(parseFloat(e.target.value) || 0)}
              className="w-full mt-1 bg-dark-bg border border-dark-border px-3 py-2 rounded-lg font-mono font-bold text-lg text-white outline-none focus:border-primary-500"
            />
          </div>
          <div className="mt-4 flex justify-between items-center text-xs">
            <span className="text-emerald-400 flex items-center gap-1 font-medium"><TrendingUp size={14} /> Tiempo Real</span>
            <button
              onClick={() => { setActiveRate('airtm'); toast.success('Tasa predeterminada cambiada a Airtm USD'); }}
              className={`px-2.5 py-1 rounded text-xs font-medium ${
                activeRate === 'airtm' ? 'bg-primary-600 text-white' : 'bg-dark-bg text-dark-muted hover:text-white'
              }`}
            >
              {activeRate === 'airtm' ? '✓ Activa' : 'Fijar Activa'}
            </button>
          </div>
        </div>
      </div>

      {/* Configuration & Rules */}
      <div className="bg-dark-card border border-dark-border rounded-xl p-6">
        <h3 className="text-lg font-semibold text-white mb-4">Políticas de Conversión Global</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="p-4 bg-dark-bg rounded-lg border border-dark-border">
            <p className="font-semibold text-white text-sm">Frecuencia de Actualización</p>
            <p className="text-xs text-dark-muted mt-1">Sincronización automática de BCV y Binance cada 15 minutos vía Edge Function.</p>
            <span className="inline-block mt-3 text-xs bg-emerald-500/20 text-emerald-400 px-2 py-0.5 rounded font-medium">Cada 15 min</span>
          </div>

          <div className="p-4 bg-dark-bg rounded-lg border border-dark-border">
            <p className="font-semibold text-white text-sm">Tolerancia de Desvío</p>
            <p className="text-xs text-dark-muted mt-1">Alerta automática al administrador si la tasa libre se desvía más de un 25% del oficial.</p>
            <span className="inline-block mt-3 text-xs bg-amber-500/20 text-amber-400 px-2 py-0.5 rounded font-medium">Límite 25%</span>
          </div>

          <div className="p-4 bg-dark-bg rounded-lg border border-dark-border">
            <p className="font-semibold text-white text-sm">Seguridad y Caching</p>
            <p className="text-xs text-dark-muted mt-1">Fallback en caché local si la API externa no responde para evitar caídas.</p>
            <span className="inline-block mt-3 text-xs bg-blue-500/20 text-blue-400 px-2 py-0.5 rounded font-medium">Fallback Activo</span>
          </div>
        </div>
      </div>
    </div>
  );
};
