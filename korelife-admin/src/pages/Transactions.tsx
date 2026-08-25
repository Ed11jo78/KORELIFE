import React, { useState } from 'react';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { SearchBar } from '../components/ui/SearchBar';
import { Table } from '../components/ui/Table';
import { Pagination } from '../components/ui/Pagination';
import { Modal } from '../components/ui/Modal';
import { ArrowDownLeft, ArrowUpRight, RefreshCw, DollarSign, Wallet, CheckCircle2, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

interface GlobalTransaction {
  id: string;
  userName: string;
  userEmail: string;
  accountName: string;
  amount: number;
  currency: 'VES' | 'USD' | 'EUR';
  type: 'income' | 'expense' | 'transfer';
  category: string;
  description: string;
  date: string;
  status: 'completed' | 'pending' | 'flagged';
}

const initialTransactions: GlobalTransaction[] = [
  { id: 'TX-901', userName: 'Eduardo José', userEmail: 'eduardo@korelife.com', accountName: 'Banesco (Bolívares)', amount: 85000.00, currency: 'VES', type: 'income', category: 'Nómina', description: 'Pago de quincena corporativa', date: '2026-08-24 14:20', status: 'completed' },
  { id: 'TX-902', userName: 'Eduardo José', userEmail: 'eduardo@korelife.com', accountName: 'Banesco (Bolívares)', amount: 3500.00, currency: 'VES', type: 'expense', category: 'Supermercado', description: 'Compras de víveres', date: '2026-08-24 12:15', status: 'completed' },
  { id: 'TX-903', userName: 'María González', userEmail: 'maria.g@gmail.com', accountName: 'Binance USDT', amount: 150.00, currency: 'USD', type: 'income', category: 'Freelance', description: 'Diseño UX/UI App', date: '2026-08-24 10:45', status: 'completed' },
  { id: 'TX-904', userName: 'Carlos Mendoza', userEmail: 'carlos.m@hotmail.com', accountName: 'Efectivo USD', amount: 50.00, currency: 'USD', type: 'expense', category: 'Servicios', description: 'Pago de Fibra Óptica', date: '2026-08-23 18:30', status: 'completed' },
  { id: 'TX-905', userName: 'Ana Rodríguez', userEmail: 'ana.r@yahoo.com', accountName: 'Mercantil (Bolívares)', amount: 12400.00, currency: 'VES', type: 'expense', category: 'Farmacia', description: 'Medicinas y cuidado', date: '2026-08-23 16:10', status: 'completed' },
  { id: 'TX-906', userName: 'Pedro Silva', userEmail: 'pedro.silva@empresa.com', accountName: 'Zinli Panamá', amount: 45.00, currency: 'USD', type: 'expense', category: 'Suscripciones', description: 'Hosting y Dominio Web', date: '2026-08-23 11:00', status: 'completed' },
  { id: 'TX-907', userName: 'Valeria Díaz', userEmail: 'valeria@gmail.com', accountName: 'Pago Móvil BDV', amount: 6500.00, currency: 'VES', type: 'transfer', category: 'Transferencia', description: 'Ahorro personal', date: '2026-08-22 20:00', status: 'completed' },
];

export const Transactions: React.FC = () => {
  const [transactions, setTransactions] = useState<GlobalTransaction[]>(initialTransactions);
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('all');
  const [currencyFilter, setCurrencyFilter] = useState('all');
  const [page, setPage] = useState(1);
  const [selectedTx, setSelectedTx] = useState<GlobalTransaction | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);

  const filtered = transactions.filter((tx) => {
    const matchSearch = tx.userName.toLowerCase().includes(search.toLowerCase()) ||
      tx.userEmail.toLowerCase().includes(search.toLowerCase()) ||
      tx.description.toLowerCase().includes(search.toLowerCase()) ||
      tx.category.toLowerCase().includes(search.toLowerCase());
    const matchType = typeFilter === 'all' || tx.type === typeFilter;
    const matchCurrency = currencyFilter === 'all' || tx.currency === currencyFilter;
    return matchSearch && matchType && matchCurrency;
  });

  const totalVes = transactions
    .filter(t => t.currency === 'VES' && t.type === 'income')
    .reduce((sum, t) => sum + t.amount, 0);

  const totalUsd = transactions
    .filter(t => t.currency === 'USD' && t.type === 'income')
    .reduce((sum, t) => sum + t.amount, 0);

  const handleDelete = (id: string) => {
    setTransactions(prev => prev.filter(t => t.id !== id));
    toast.success('Transacción eliminada del registro de auditoría');
    setIsDetailModalOpen(false);
  };

  const columns = [
    {
      header: 'Usuario',
      accessor: (item: GlobalTransaction) => (
        <div>
          <p className="font-semibold text-white">{item.userName}</p>
          <p className="text-xs text-dark-muted">{item.userEmail}</p>
        </div>
      )
    },
    {
      header: 'Tipo',
      accessor: (item: GlobalTransaction) => (
        <span className={`inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium ${
          item.type === 'income' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' :
          item.type === 'expense' ? 'bg-rose-500/20 text-rose-400 border border-rose-500/30' :
          'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30'
        }`}>
          {item.type === 'income' && <ArrowDownLeft size={14} />}
          {item.type === 'expense' && <ArrowUpRight size={14} />}
          {item.type === 'transfer' && <RefreshCw size={14} />}
          {item.type === 'income' ? 'Ingreso' : item.type === 'expense' ? 'Gasto' : 'Transferencia'}
        </span>
      )
    },
    {
      header: 'Categoría & Detalle',
      accessor: (item: GlobalTransaction) => (
        <div>
          <p className="font-medium text-slate-200">{item.category}</p>
          <p className="text-xs text-dark-muted truncate max-w-xs">{item.description}</p>
        </div>
      )
    },
    {
      header: 'Cuenta',
      accessor: (item: GlobalTransaction) => (
        <span className="text-xs font-mono bg-dark-bg px-2 py-1 rounded border border-dark-border text-slate-300">
          {item.accountName}
        </span>
      )
    },
    {
      header: 'Monto',
      accessor: (item: GlobalTransaction) => (
        <div className="text-right">
          <p className={`font-bold font-mono text-sm ${
            item.type === 'income' ? 'text-emerald-400' : 'text-slate-100'
          }`}>
            {item.type === 'income' ? '+' : '-'}{item.amount.toLocaleString('es-VE', { minimumFractionDigits: 2 })} {item.currency}
          </p>
          <p className="text-xs text-dark-muted">{item.date}</p>
        </div>
      )
    },
    {
      header: 'Acciones',
      accessor: (item: GlobalTransaction) => (
        <div className="flex gap-2 justify-end">
          <Button
            variant="secondary"
            className="py-1 px-3 text-xs"
            onClick={() => { setSelectedTx(item); setIsDetailModalOpen(true); }}
          >
            Ver Detalle
          </Button>
        </div>
      )
    }
  ];

  return (
    <div className="space-y-6">
      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div className="bg-dark-card border border-dark-border p-5 rounded-xl">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-xs text-dark-muted uppercase font-semibold">Total Transacciones</p>
              <h3 className="text-2xl font-bold text-white mt-1">{transactions.length}</h3>
            </div>
            <div className="p-2.5 bg-primary-600/20 rounded-lg text-primary-400">
              <RefreshCw size={22} />
            </div>
          </div>
          <p className="text-xs text-emerald-400 mt-3 font-medium">100% Verificadas en DB</p>
        </div>

        <div className="bg-dark-card border border-dark-border p-5 rounded-xl">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-xs text-dark-muted uppercase font-semibold">Volumen Ingresos VES</p>
              <h3 className="text-2xl font-bold text-emerald-400 mt-1">{totalVes.toLocaleString('es-VE')} Bs.</h3>
            </div>
            <div className="p-2.5 bg-emerald-500/20 rounded-lg text-emerald-400">
              <Wallet size={22} />
            </div>
          </div>
          <p className="text-xs text-dark-muted mt-3 font-medium">Cuentas nacionales</p>
        </div>

        <div className="bg-dark-card border border-dark-border p-5 rounded-xl">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-xs text-dark-muted uppercase font-semibold">Volumen Ingresos USD</p>
              <h3 className="text-2xl font-bold text-cyan-400 mt-1">${totalUsd.toLocaleString('en-US', { minimumFractionDigits: 2 })}</h3>
            </div>
            <div className="p-2.5 bg-cyan-500/20 rounded-lg text-cyan-400">
              <DollarSign size={22} />
            </div>
          </div>
          <p className="text-xs text-dark-muted mt-3 font-medium">Binance / Zinli / Efectivo</p>
        </div>

        <div className="bg-dark-card border border-dark-border p-5 rounded-xl">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-xs text-dark-muted uppercase font-semibold">Estado de Auditoría</p>
              <h3 className="text-2xl font-bold text-white mt-1">Activo</h3>
            </div>
            <div className="p-2.5 bg-emerald-500/20 rounded-lg text-emerald-400">
              <CheckCircle2 size={22} />
            </div>
          </div>
          <p className="text-xs text-emerald-400 mt-3 font-medium">Row Level Security Protegido</p>
        </div>
      </div>

      {/* Main Table */}
      <div className="bg-dark-card rounded-xl border border-dark-border overflow-hidden p-6">
        <div className="flex flex-col lg:flex-row lg:justify-between lg:items-center gap-4 mb-6">
          <div>
            <h3 className="text-lg font-semibold text-white">Auditoría Global de Transacciones</h3>
            <p className="text-xs text-dark-muted">Supervisa todos los movimientos monetarios de la plataforma</p>
          </div>

          <div className="flex flex-wrap gap-2">
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="px-3 py-2 bg-dark-bg border border-dark-border rounded-md text-xs text-white outline-none"
            >
              <option value="all">Todos los Tipos</option>
              <option value="income">Solo Ingresos</option>
              <option value="expense">Solo Gastos</option>
              <option value="transfer">Transferencias</option>
            </select>

            <select
              value={currencyFilter}
              onChange={(e) => setCurrencyFilter(e.target.value)}
              className="px-3 py-2 bg-dark-bg border border-dark-border rounded-md text-xs text-white outline-none"
            >
              <option value="all">Todas las Monedas</option>
              <option value="VES">Bolívares (VES)</option>
              <option value="USD">Dólares (USD)</option>
              <option value="EUR">Euros (EUR)</option>
            </select>

            <SearchBar
              value={search}
              onChange={setSearch}
              placeholder="Buscar por usuario, concepto..."
              className="w-64"
            />
          </div>
        </div>

        <Table
          data={filtered.slice((page - 1) * 6, page * 6)}
          columns={columns}
          keyExtractor={(t) => t.id}
        />

        <div className="mt-4">
          <Pagination
            currentPage={page}
            totalPages={Math.max(1, Math.ceil(filtered.length / 6))}
            onPageChange={setPage}
          />
        </div>
      </div>

      {/* Modal Detalle Transacción */}
      <Modal
        isOpen={isDetailModalOpen}
        onClose={() => setIsDetailModalOpen(false)}
        title="Detalles de Transacción #Auditoría"
      >
        {selectedTx && (
          <div className="space-y-4 text-sm">
            <div className="p-4 bg-dark-bg rounded-lg border border-dark-border space-y-3">
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">ID de Transacción:</span>
                <span className="font-mono text-white font-bold">{selectedTx.id}</span>
              </div>
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">Usuario:</span>
                <span className="text-white font-medium">{selectedTx.userName} ({selectedTx.userEmail})</span>
              </div>
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">Cuenta Afectada:</span>
                <span className="text-white">{selectedTx.accountName}</span>
              </div>
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">Categoría:</span>
                <span className="text-white">{selectedTx.category}</span>
              </div>
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">Descripción:</span>
                <span className="text-white">{selectedTx.description}</span>
              </div>
              <div className="flex justify-between items-center border-b border-dark-border pb-2">
                <span className="text-dark-muted">Monto Registrado:</span>
                <span className="text-emerald-400 font-bold font-mono text-base">
                  {selectedTx.amount.toLocaleString('es-VE', { minimumFractionDigits: 2 })} {selectedTx.currency}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-dark-muted">Fecha y Hora:</span>
                <span className="text-white">{selectedTx.date}</span>
              </div>
            </div>

            <div className="flex justify-between items-center pt-2">
              <Button
                variant="secondary"
                className="text-rose-400 hover:text-rose-300 border-rose-500/30 flex items-center gap-2"
                onClick={() => handleDelete(selectedTx.id)}
              >
                <Trash2 size={16} /> Eliminar Movimiento
              </Button>
              <Button
                variant="primary"
                onClick={() => setIsDetailModalOpen(false)}
              >
                Cerrar
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
