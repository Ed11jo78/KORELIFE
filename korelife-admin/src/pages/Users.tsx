import React, { useState } from 'react';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { SearchBar } from '../components/ui/SearchBar';
import { Table } from '../components/ui/Table';
import { Pagination } from '../components/ui/Pagination';
import { Modal } from '../components/ui/Modal';
import { Shield, Sparkles, UserCheck, UserX, Key, Wallet, CheckSquare, Mail } from 'lucide-react';
import toast from 'react-hot-toast';

interface AdminUser {
  id: string;
  name: string;
  email: string;
  plan: 'free' | 'premium' | 'family' | 'team';
  status: 'active' | 'inactive' | 'suspended';
  currency: 'VES' | 'USD' | 'EUR';
  totalBalanceVES: number;
  totalBalanceUSD: number;
  accountsCount: number;
  tasksCount: number;
  date: string;
}

const initialUsers: AdminUser[] = [
  { id: 'usr_001', name: 'Eduardo José (Admin)', email: 'ed11jo78@gmail.com', plan: 'team', status: 'active', currency: 'VES', totalBalanceVES: 232700.50, totalBalanceUSD: 770.00, accountsCount: 4, tasksCount: 5, date: '2026-08-20' },
  { id: 'usr_002', name: 'María González', email: 'maria.g@gmail.com', plan: 'premium', status: 'active', currency: 'USD', totalBalanceVES: 15400.00, totalBalanceUSD: 1250.00, accountsCount: 3, tasksCount: 8, date: '2026-08-21' },
  { id: 'usr_003', name: 'Carlos Mendoza', email: 'carlos.m@hotmail.com', plan: 'free', status: 'active', currency: 'VES', totalBalanceVES: 48900.00, totalBalanceUSD: 120.00, accountsCount: 2, tasksCount: 3, date: '2026-08-22' },
  { id: 'usr_004', name: 'Ana Rodríguez', email: 'ana.r@yahoo.com', plan: 'family', status: 'active', currency: 'VES', totalBalanceVES: 92000.00, totalBalanceUSD: 340.00, accountsCount: 3, tasksCount: 6, date: '2026-08-22' },
  { id: 'usr_005', name: 'Pedro Silva', email: 'pedro.silva@empresa.com', plan: 'free', status: 'inactive', currency: 'USD', totalBalanceVES: 0.00, totalBalanceUSD: 45.00, accountsCount: 1, tasksCount: 1, date: '2026-08-23' },
  { id: 'usr_006', name: 'Valeria Díaz', email: 'valeria.d@gmail.com', plan: 'free', status: 'active', currency: 'VES', totalBalanceVES: 18500.00, totalBalanceUSD: 50.00, accountsCount: 2, tasksCount: 4, date: '2026-08-24' },
];

export const Users: React.FC = () => {
  const [users, setUsers] = useState<AdminUser[]>(initialUsers);
  const [search, setSearch] = useState('');
  const [planFilter, setPlanFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [page, setPage] = useState(1);
  const [selectedUser, setSelectedUser] = useState<AdminUser | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isPlanModalOpen, setIsPlanModalOpen] = useState(false);

  const filtered = users.filter((u) => {
    const matchSearch = u.name.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase()) ||
      u.id.toLowerCase().includes(search.toLowerCase());
    const matchPlan = planFilter === 'all' || u.plan === planFilter;
    const matchStatus = statusFilter === 'all' || u.status === statusFilter;
    return matchSearch && matchPlan && matchStatus;
  });

  const handleUpdatePlan = (newPlan: 'free' | 'premium' | 'family' | 'team') => {
    if (!selectedUser) return;
    setUsers(prev => prev.map(u => u.id === selectedUser.id ? { ...u, plan: newPlan } : u));
    toast.success(`Plan de ${selectedUser.name} actualizado a ${newPlan.toUpperCase()}`);
    setIsPlanModalOpen(false);
  };

  const handleToggleStatus = (user: AdminUser) => {
    const newStatus = user.status === 'active' ? 'suspended' : 'active';
    setUsers(prev => prev.map(u => u.id === user.id ? { ...u, status: newStatus } : u));
    toast.success(`Usuario ${user.name} ahora está ${newStatus === 'active' ? 'ACTIVO' : 'SUSPENDIDO'}`);
  };

  const handleSendResetPassword = (email: string) => {
    toast.success(`Enlace de restablecimiento de contraseña enviado a ${email}`);
  };

  const columns = [
    {
      header: 'Usuario',
      accessor: (item: AdminUser) => (
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-primary-600 to-indigo-500 flex items-center justify-center text-xs font-bold text-white shadow-md">
            {item.name.charAt(0)}
          </div>
          <div>
            <p className="font-semibold text-white text-sm">{item.name}</p>
            <p className="text-dark-muted text-xs font-mono">{item.email}</p>
          </div>
        </div>
      )
    },
    {
      header: 'Plan Actual',
      accessor: (item: AdminUser) => (
        <Badge color={
          item.plan === 'team' ? 'bg-purple-500/20 text-purple-400 border border-purple-500/30' :
          item.plan === 'family' ? 'bg-blue-500/20 text-blue-400 border border-blue-500/30' :
          item.plan === 'premium' ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' :
          'bg-slate-500/20 text-slate-400 border border-slate-500/30'
        }>
          {item.plan.toUpperCase()}
        </Badge>
      )
    },
    {
      header: 'Patrimonio Auditado',
      accessor: (item: AdminUser) => (
        <div>
          <p className="text-xs font-bold text-emerald-400 font-mono">
            {item.totalBalanceVES.toLocaleString('es-VE')} Bs.
          </p>
          <p className="text-xs text-dark-muted font-mono">
            ${item.totalBalanceUSD.toLocaleString('en-US', { minimumFractionDigits: 2 })}
          </p>
        </div>
      )
    },
    {
      header: 'Cuentas / Tareas',
      accessor: (item: AdminUser) => (
        <span className="text-xs text-slate-300">
          🏦 {item.accountsCount} ctas | 📋 {item.tasksCount} tareas
        </span>
      )
    },
    {
      header: 'Estado',
      accessor: (item: AdminUser) => (
        <Badge color={item.status === 'active' ? 'bg-emerald-500' : 'bg-red-500'}>
          {item.status === 'active' ? 'Activo' : 'Suspendido'}
        </Badge>
      )
    },
    {
      header: 'Acciones',
      accessor: (item: AdminUser) => (
        <div className="flex gap-2 justify-end">
          <Button
            variant="secondary"
            className="py-1 px-2.5 text-xs"
            onClick={() => { setSelectedUser(item); setIsDetailModalOpen(true); }}
          >
            Auditar
          </Button>
          <Button
            variant="primary"
            className="py-1 px-2.5 text-xs flex items-center gap-1"
            onClick={() => { setSelectedUser(item); setIsPlanModalOpen(true); }}
          >
            <Sparkles size={13} /> Plan
          </Button>
        </div>
      )
    }
  ];

  return (
    <div className="space-y-6">
      <div className="bg-dark-card rounded-xl border border-dark-border overflow-hidden p-6">
        <div className="flex flex-col lg:flex-row lg:justify-between lg:items-center gap-4 mb-6">
          <div>
            <h3 className="text-lg font-semibold text-white">Gestión Integral de Usuarios</h3>
            <p className="text-xs text-dark-muted">Control de membresías, permisos y auditoría de patrimonio</p>
          </div>

          <div className="flex flex-wrap gap-2">
            <select
              value={planFilter}
              onChange={(e) => setPlanFilter(e.target.value)}
              className="px-3 py-2 bg-dark-bg border border-dark-border rounded-md text-xs text-white outline-none"
            >
              <option value="all">Todos los Planes</option>
              <option value="free">Free</option>
              <option value="premium">Premium</option>
              <option value="family">Family</option>
              <option value="team">Team / Staff</option>
            </select>

            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-2 bg-dark-bg border border-dark-border rounded-md text-xs text-white outline-none"
            >
              <option value="all">Todos los Estados</option>
              <option value="active">Activos</option>
              <option value="suspended">Suspendidos</option>
            </select>

            <SearchBar
              value={search}
              onChange={setSearch}
              placeholder="Buscar por nombre, correo, ID..."
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

      {/* Modal Auditar Usuario */}
      <Modal
        isOpen={isDetailModalOpen}
        onClose={() => setIsDetailModalOpen(false)}
        title="Ficha de Auditoría de Usuario"
      >
        {selectedUser && (
          <div className="space-y-4 text-sm">
            <div className="p-4 bg-dark-bg rounded-lg border border-dark-border space-y-3">
              <div className="flex items-center gap-3 pb-3 border-b border-dark-border">
                <div className="w-12 h-12 rounded-full bg-primary-600 flex items-center justify-center text-lg font-bold text-white">
                  {selectedUser.name.charAt(0)}
                </div>
                <div>
                  <h4 className="text-base font-bold text-white">{selectedUser.name}</h4>
                  <p className="text-xs text-dark-muted">{selectedUser.email}</p>
                  <span className="text-xs font-mono text-primary-400">ID: {selectedUser.id}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-2 pt-1 text-xs">
                <div className="p-3 bg-dark-card rounded border border-dark-border">
                  <p className="text-dark-muted">Patrimonio en Bolívares</p>
                  <p className="text-base font-bold text-emerald-400 mt-0.5">{selectedUser.totalBalanceVES.toLocaleString('es-VE')} Bs.</p>
                </div>
                <div className="p-3 bg-dark-card rounded border border-dark-border">
                  <p className="text-dark-muted">Patrimonio en Dólares</p>
                  <p className="text-base font-bold text-cyan-400 mt-0.5">${selectedUser.totalBalanceUSD.toLocaleString('en-US', { minimumFractionDigits: 2 })}</p>
                </div>
              </div>

              <div className="pt-2 space-y-2 text-xs">
                <div className="flex justify-between text-dark-muted">
                  <span>Moneda Principal:</span>
                  <span className="text-white font-bold">{selectedUser.currency}</span>
                </div>
                <div className="flex justify-between text-dark-muted">
                  <span>Cuentas Registradas:</span>
                  <span className="text-white font-bold">{selectedUser.accountsCount} cuentas</span>
                </div>
                <div className="flex justify-between text-dark-muted">
                  <span>Tareas & Hábitos:</span>
                  <span className="text-white font-bold">{selectedUser.tasksCount} tareas activas</span>
                </div>
                <div className="flex justify-between text-dark-muted">
                  <span>Fecha de Registro:</span>
                  <span className="text-white font-bold">{selectedUser.date}</span>
                </div>
              </div>
            </div>

            <div className="flex flex-wrap gap-2 justify-between pt-2">
              <Button
                variant="secondary"
                className="text-xs flex items-center gap-1 text-amber-400 hover:text-amber-300"
                onClick={() => handleSendResetPassword(selectedUser.email)}
              >
                <Key size={14} /> Resetear Password
              </Button>

              <Button
                variant="secondary"
                className={`text-xs flex items-center gap-1 ${
                  selectedUser.status === 'active' ? 'text-red-400 hover:text-red-300' : 'text-emerald-400 hover:text-emerald-300'
                }`}
                onClick={() => { handleToggleStatus(selectedUser); setIsDetailModalOpen(false); }}
              >
                {selectedUser.status === 'active' ? <UserX size={14} /> : <UserCheck size={14} />}
                {selectedUser.status === 'active' ? 'Suspender Usuario' : 'Activar Usuario'}
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Modal Asignar Plan */}
      <Modal
        isOpen={isPlanModalOpen}
        onClose={() => setIsPlanModalOpen(false)}
        title="Asignar Plan / Membresía"
      >
        {selectedUser && (
          <div className="space-y-4 text-sm">
            <p className="text-slate-300">
              Selecciona el nuevo nivel de membresía para <strong>{selectedUser.name}</strong>:
            </p>

            <div className="grid grid-cols-2 gap-3">
              <button
                onClick={() => handleUpdatePlan('free')}
                className={`p-3 rounded-lg border text-left transition-all ${
                  selectedUser.plan === 'free' ? 'border-primary-500 bg-primary-950/30' : 'border-dark-border bg-dark-bg hover:border-slate-500'
                }`}
              >
                <p className="font-bold text-white">Free</p>
                <p className="text-xs text-dark-muted mt-1">Hasta 3 cuentas y funciones básicas.</p>
              </button>

              <button
                onClick={() => handleUpdatePlan('premium')}
                className={`p-3 rounded-lg border text-left transition-all ${
                  selectedUser.plan === 'premium' ? 'border-emerald-500 bg-emerald-950/30' : 'border-dark-border bg-dark-bg hover:border-slate-500'
                }`}
              >
                <p className="font-bold text-emerald-400">Premium Pro</p>
                <p className="text-xs text-dark-muted mt-1">Cuentas ilimitadas, alertas y analíticas.</p>
              </button>

              <button
                onClick={() => handleUpdatePlan('family')}
                className={`p-3 rounded-lg border text-left transition-all ${
                  selectedUser.plan === 'family' ? 'border-blue-500 bg-blue-950/30' : 'border-dark-border bg-dark-bg hover:border-slate-500'
                }`}
              >
                <p className="font-bold text-blue-400">Family Plan</p>
                <p className="text-xs text-dark-muted mt-1">Hasta 5 miembros del hogar compartidos.</p>
              </button>

              <button
                onClick={() => handleUpdatePlan('team')}
                className={`p-3 rounded-lg border text-left transition-all ${
                  selectedUser.plan === 'team' ? 'border-purple-500 bg-purple-950/30' : 'border-dark-border bg-dark-bg hover:border-slate-500'
                }`}
              >
                <p className="font-bold text-purple-400">Team / Staff</p>
                <p className="text-xs text-dark-muted mt-1">Acceso corporativo y soporte prioritario.</p>
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
