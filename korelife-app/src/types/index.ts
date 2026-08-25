export type CurrencyCode = 'VES' | 'USD' | 'EUR';

export type RateProvider = 'bcv_usd' | 'bcv_eur' | 'binance' | 'airtm';

export type ExchangeRate = {
  id: RateProvider;
  name: string;
  symbol: string;
  rate: number;
  change24h?: number;
  lastUpdated: string;
  icon: string;
};

export type User = {
  id: string;
  email: string;
  full_name: string;
  avatar_url?: string;
  currency: CurrencyCode;
  created_at: string;
};

export type Account = {
  id: string;
  user_id: string;
  name: string;
  type: 'bank' | 'credit' | 'cash' | 'investment';
  balance: number;
  currency: CurrencyCode;
  created_at: string;
};

export type Transaction = {
  id: string;
  account_id: string;
  user_id: string;
  amount: number;
  currency: CurrencyCode;
  amount_secondary?: number;
  type: 'income' | 'expense' | 'transfer';
  category: string;
  description: string;
  date: string;
  created_at: string;
};

export type Budget = {
  id: string;
  user_id: string;
  category: string;
  amount: number;
  currency: CurrencyCode;
  spent: number;
  period: 'monthly' | 'weekly';
  created_at: string;
};

export type Task = {
  id: string;
  user_id: string;
  title: string;
  description?: string;
  completed: boolean;
  due_date?: string;
  priority: 'low' | 'medium' | 'high';
  created_at: string;
};

export type Habit = {
  id: string;
  user_id: string;
  name: string;
  streak: number;
  completed_today: boolean;
  frequency: 'daily' | 'weekly';
  created_at: string;
};

export type Note = {
  id: string;
  user_id: string;
  title: string;
  content: string;
  created_at: string;
};