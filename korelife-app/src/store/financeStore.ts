import { create } from 'zustand';
import { Account, Transaction, Budget, CurrencyCode } from '../types';
import { useRatesStore } from './ratesStore';
import { useAuthStore } from './authStore';
import { supabase } from '../lib/supabase';

const DEMO_ACCOUNTS: Account[] = [
  { id: '1', user_id: 'demo', name: 'Banesco (Bolívares)', type: 'bank', balance: 145200.50, currency: 'VES', created_at: new Date().toISOString() },
  { id: '2', user_id: 'demo', name: 'Mercantil (Bolívares)', type: 'bank', balance: 87500.00, currency: 'VES', created_at: new Date().toISOString() },
  { id: '3', user_id: 'demo', name: 'Efectivo USD ($)', type: 'cash', balance: 450.00, currency: 'USD', created_at: new Date().toISOString() },
  { id: '4', user_id: 'demo', name: 'Binance USDT', type: 'investment', balance: 320.00, currency: 'USD', created_at: new Date().toISOString() }
];

const DEMO_TRANSACTIONS: Transaction[] = [
  { id: 't1', account_id: '1', user_id: 'demo', amount: 3500.00, currency: 'VES', type: 'expense', category: 'Supermercado', description: 'Compras de la semana', date: '2026-08-20', created_at: new Date().toISOString() },
  { id: 't2', account_id: '1', user_id: 'demo', amount: 85000.00, currency: 'VES', type: 'income', category: 'Nómina', description: 'Pago de quincena', date: '2026-08-15', created_at: new Date().toISOString() },
  { id: 't3', account_id: '3', user_id: 'demo', amount: 50.00, currency: 'USD', type: 'expense', category: 'Servicios', description: 'Internet fibra óptica', date: '2026-08-18', created_at: new Date().toISOString() },
  { id: 't4', account_id: '4', user_id: 'demo', amount: 150.00, currency: 'USD', type: 'income', category: 'Freelance', description: 'Diseño UX/UI Proyecto', date: '2026-08-19', created_at: new Date().toISOString() }
];

const DEMO_BUDGETS: Budget[] = [
  { id: 'b1', user_id: 'demo', category: 'Alimentación', amount: 25000, spent: 14200, currency: 'VES', period: 'monthly', created_at: new Date().toISOString() },
  { id: 'b2', user_id: 'demo', category: 'Transporte & Gasolina', amount: 8000, spent: 3400, currency: 'VES', period: 'monthly', created_at: new Date().toISOString() },
  { id: 'b3', user_id: 'demo', category: 'Servicios del Hogar', amount: 6000, spent: 5800, currency: 'VES', period: 'monthly', created_at: new Date().toISOString() }
];

type FinanceState = {
  accounts: Account[];
  transactions: Transaction[];
  budgets: Budget[];
  loading: boolean;
  fetchData: () => Promise<void>;
  addTransaction: (tx: Omit<Transaction, 'id' | 'created_at'>) => void;
  addAccount: (acc: Omit<Account, 'id' | 'created_at'>) => void;
  getTotalBalanceInVes: () => number;
  getTotalBalanceInUsd: () => number;
};

export const useFinanceStore = create<FinanceState>((set, get) => ({
  accounts: [],
  transactions: [],
  budgets: [],
  loading: false,

  fetchData: async () => {
    set({ loading: true });
    const { user } = useAuthStore.getState();

    if (user && user.id) {
      // USUARIO REAL REGISTRADO:
      try {
        const { data: accountsData } = await supabase
          .from('accounts')
          .select('*')
          .eq('user_id', user.id);

        const { data: txData } = await supabase
          .from('transactions')
          .select('*')
          .eq('user_id', user.id)
          .order('created_at', { ascending: false });

        if (accountsData && accountsData.length > 0) {
          set({
            accounts: accountsData,
            transactions: txData || [],
            loading: false
          });
        } else {
          // Usuario nuevo: Empezar con cuenta limpia en 0 con la moneda seleccionada
          const userCurrency = (user.user_metadata?.currency as CurrencyCode) || 'VES';
          const defaultAccount: Account = {
            id: 'acc_' + Date.now(),
            user_id: user.id,
            name: 'Cuenta Principal',
            type: 'bank',
            balance: 0.00,
            currency: userCurrency,
            created_at: new Date().toISOString()
          };

          set({
            accounts: [defaultAccount],
            transactions: [],
            budgets: [],
            loading: false
          });
        }
      } catch (err) {
        // Si Supabase no responde o es offline, mantener datos limpios del usuario
        set((state) => {
          if (state.accounts.length === 0) {
            const userCurrency = (user.user_metadata?.currency as CurrencyCode) || 'VES';
            return {
              accounts: [{
                id: 'acc_' + Date.now(),
                user_id: user.id,
                name: 'Cuenta Principal',
                type: 'bank',
                balance: 0.00,
                currency: userCurrency,
                created_at: new Date().toISOString()
              }],
              transactions: [],
              budgets: [],
              loading: false
            };
          }
          return { loading: false };
        });
      }
    } else {
      // MODO DEMO / MODO RÁPIDO: Mostrar datos de ejemplo precargados
      set({
        accounts: DEMO_ACCOUNTS,
        transactions: DEMO_TRANSACTIONS,
        budgets: DEMO_BUDGETS,
        loading: false
      });
    }
  },

  addTransaction: (tx) => {
    const { user } = useAuthStore.getState();
    const newTx: Transaction = {
      ...tx,
      user_id: user?.id || 'demo',
      id: 'tx_' + Date.now(),
      created_at: new Date().toISOString()
    };
    
    // Actualizar balance de la cuenta
    set((state) => {
      const updatedAccounts = state.accounts.map((acc) => {
        if (acc.id === tx.account_id) {
          const delta = tx.type === 'income' ? tx.amount : -tx.amount;
          return { ...acc, balance: Math.max(0, acc.balance + delta) };
        }
        return acc;
      });

      return {
        transactions: [newTx, ...state.transactions],
        accounts: updatedAccounts
      };
    });

    // Guardar en Supabase si hay usuario conectado
    if (user?.id) {
      supabase.from('transactions').insert(newTx).then(() => {}).catch(() => {});
    }
  },

  addAccount: (acc) => {
    const { user } = useAuthStore.getState();
    const newAcc: Account = {
      ...acc,
      user_id: user?.id || 'demo',
      id: 'acc_' + Date.now(),
      created_at: new Date().toISOString()
    };
    set((state) => ({ accounts: [...state.accounts, newAcc] }));

    if (user?.id) {
      supabase.from('accounts').insert(newAcc).then(() => {}).catch(() => {});
    }
  },

  getTotalBalanceInVes: () => {
    const accounts = get().accounts;
    const { convert } = useRatesStore.getState();
    return accounts.reduce((sum, acc) => {
      const inVes = convert(acc.balance, acc.currency, 'VES');
      return sum + inVes;
    }, 0);
  },

  getTotalBalanceInUsd: () => {
    const accounts = get().accounts;
    const { convert } = useRatesStore.getState();
    return accounts.reduce((sum, acc) => {
      const inUsd = convert(acc.balance, acc.currency, 'USD');
      return sum + inUsd;
    }, 0);
  }
}));