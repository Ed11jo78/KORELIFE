import React, { useMemo } from 'react';
import { View, Text, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { TrendingUp, TrendingDown, Minus, BarChart2, PieChart, ArrowUpRight } from 'lucide-react-native';
import { useFinanceStore } from '../../../src/store/financeStore';
import { useRatesStore } from '../../../src/store/ratesStore';

const CATEGORY_COLORS: Record<string, string> = {
  'Alimentación': '#10b981',
  'Supermercado': '#10b981',
  'Transporte & Gasolina': '#6366f1',
  'Servicios del Hogar': '#f59e0b',
  'Nómina': '#22d3ee',
  'Freelance': '#a78bfa',
  'Servicios': '#f87171',
  'Entretenimiento': '#fb923c',
  'Salud': '#34d399',
  'Educación': '#60a5fa',
};

export default function AnalyticsScreen() {
  const transactions = useFinanceStore((s) => s.transactions);
  const { convert, formatCurrency, rates, selectedProvider } = useRatesStore();

  // Aggregations
  const stats = useMemo(() => {
    const now = new Date();
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];

    const monthTxs = transactions.filter((t) => t.date >= startOfMonth);

    let income = 0;
    let expenses = 0;
    const byCategory: Record<string, number> = {};

    monthTxs.forEach((t) => {
      const inVes = convert(t.amount, t.currency, 'VES');
      if (t.type === 'income') income += inVes;
      if (t.type === 'expense') {
        expenses += inVes;
        byCategory[t.category] = (byCategory[t.category] || 0) + inVes;
      }
    });

    const savings = income - expenses;
    const savingsRate = income > 0 ? (savings / income) * 100 : 0;

    const categories = Object.entries(byCategory)
      .map(([name, amount]) => ({ name, amount, pct: expenses > 0 ? (amount / expenses) * 100 : 0 }))
      .sort((a, b) => b.amount - a.amount);

    return { income, expenses, savings, savingsRate, categories, monthTxs };
  }, [transactions, convert]);

  const allTimeStats = useMemo(() => {
    let totalIncome = 0;
    let totalExpenses = 0;
    transactions.forEach((t) => {
      const inVes = convert(t.amount, t.currency, 'VES');
      if (t.type === 'income') totalIncome += inVes;
      if (t.type === 'expense') totalExpenses += inVes;
    });
    return { totalIncome, totalExpenses };
  }, [transactions, convert]);

  const currentRate = rates[selectedProvider]?.rate || 36.54;

  return (
    <SafeAreaView className="flex-1 bg-slate-950">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="px-5 pt-4 pb-6">
          <Text className="text-slate-400 text-xs font-semibold uppercase tracking-widest">Finanzas</Text>
          <Text className="text-white text-2xl font-black">Analíticas 📊</Text>
        </View>

        {/* Monthly Summary */}
        <View className="px-5 mb-6">
          <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Este Mes</Text>
          <View className="flex-row gap-3">
            {/* Income */}
            <View className="flex-1 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl p-4">
              <View className="flex-row items-center justify-between mb-2">
                <TrendingUp size={18} color="#10b981" />
                <Text className="text-emerald-400 text-xs font-bold">INGRESOS</Text>
              </View>
              <Text className="text-white text-lg font-black" numberOfLines={1}>
                {formatCurrency(stats.income, 'VES').replace('Bs. ', 'Bs.')}
              </Text>
              <Text className="text-emerald-400/60 text-xs mt-0.5">
                ≈ $ {(stats.income / currentRate).toFixed(2)}
              </Text>
            </View>

            {/* Expenses */}
            <View className="flex-1 bg-red-500/10 border border-red-500/20 rounded-2xl p-4">
              <View className="flex-row items-center justify-between mb-2">
                <TrendingDown size={18} color="#ef4444" />
                <Text className="text-red-400 text-xs font-bold">GASTOS</Text>
              </View>
              <Text className="text-white text-lg font-black" numberOfLines={1}>
                {formatCurrency(stats.expenses, 'VES').replace('Bs. ', 'Bs.')}
              </Text>
              <Text className="text-red-400/60 text-xs mt-0.5">
                ≈ $ {(stats.expenses / currentRate).toFixed(2)}
              </Text>
            </View>
          </View>

          {/* Savings Rate */}
          <View className="bg-slate-900 border border-slate-800 rounded-2xl p-4 mt-3">
            <View className="flex-row justify-between items-center mb-3">
              <View className="flex-row items-center gap-2">
                <BarChart2 size={18} color="#6366f1" />
                <Text className="text-white font-bold ml-2">Tasa de Ahorro</Text>
              </View>
              <Text className={`font-black text-lg ${stats.savings >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                {stats.savingsRate.toFixed(1)}%
              </Text>
            </View>
            <View className="h-2.5 bg-slate-800 rounded-full overflow-hidden">
              <View
                className={`h-full rounded-full ${stats.savingsRate >= 20 ? 'bg-emerald-500' : stats.savingsRate >= 10 ? 'bg-amber-500' : 'bg-red-500'}`}
                style={{ width: `${Math.min(100, Math.abs(stats.savingsRate))}%` }}
              />
            </View>
            <View className="flex-row justify-between mt-2">
              <Text className="text-slate-500 text-xs">
                {stats.savings >= 0 ? `Ahorrado: ${formatCurrency(stats.savings, 'VES')}` : `Déficit: ${formatCurrency(Math.abs(stats.savings), 'VES')}`}
              </Text>
              <Text className={`text-xs font-semibold ${stats.savingsRate >= 20 ? 'text-emerald-400' : stats.savingsRate >= 10 ? 'text-amber-400' : 'text-red-400'}`}>
                {stats.savingsRate >= 20 ? '✅ Excelente' : stats.savingsRate >= 10 ? '⚠️ Regular' : '❌ Bajo'}
              </Text>
            </View>
          </View>
        </View>

        {/* Bar chart (visual) */}
        <View className="px-5 mb-6">
          <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Ingresos vs Gastos</Text>
          <View className="bg-slate-900 border border-slate-800 rounded-2xl p-4">
            <View className="flex-row items-end justify-around h-24">
              {['Sem 1', 'Sem 2', 'Sem 3', 'Sem 4'].map((label, i) => {
                const incomeH = [40, 70, 50, 85][i];
                const expenseH = [30, 45, 60, 40][i];
                return (
                  <View key={label} className="items-center flex-1 mx-1">
                    <View className="flex-row items-end gap-1 h-20">
                      <View className="w-4 bg-emerald-500 rounded-t" style={{ height: `${incomeH}%` }} />
                      <View className="w-4 bg-red-500/70 rounded-t" style={{ height: `${expenseH}%` }} />
                    </View>
                    <Text className="text-slate-500 text-xs mt-1">{label}</Text>
                  </View>
                );
              })}
            </View>
            <View className="flex-row justify-center gap-6 mt-3">
              <View className="flex-row items-center gap-1.5">
                <View className="w-3 h-3 bg-emerald-500 rounded" />
                <Text className="text-slate-400 text-xs ml-1.5">Ingresos</Text>
              </View>
              <View className="flex-row items-center gap-1.5">
                <View className="w-3 h-3 bg-red-500/70 rounded" />
                <Text className="text-slate-400 text-xs ml-1.5">Gastos</Text>
              </View>
            </View>
          </View>
        </View>

        {/* Categories */}
        {stats.categories.length > 0 && (
          <View className="px-5 mb-6">
            <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Gastos por Categoría</Text>
            <View className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
              {stats.categories.map((cat, index) => (
                <View key={cat.name} className={`px-4 py-3 ${index < stats.categories.length - 1 ? 'border-b border-slate-800' : ''}`}>
                  <View className="flex-row justify-between items-center mb-1.5">
                    <View className="flex-row items-center">
                      <View
                        className="w-2.5 h-2.5 rounded-full mr-2"
                        style={{ backgroundColor: CATEGORY_COLORS[cat.name] || '#64748b' }}
                      />
                      <Text className="text-white text-sm font-semibold">{cat.name}</Text>
                    </View>
                    <View className="items-end">
                      <Text className="text-white text-sm font-bold">{cat.pct.toFixed(1)}%</Text>
                      <Text className="text-slate-500 text-xs">{formatCurrency(cat.amount, 'VES')}</Text>
                    </View>
                  </View>
                  <View className="h-1.5 bg-slate-800 rounded-full overflow-hidden">
                    <View
                      className="h-full rounded-full"
                      style={{ width: `${cat.pct}%`, backgroundColor: CATEGORY_COLORS[cat.name] || '#64748b' }}
                    />
                  </View>
                </View>
              ))}
            </View>
          </View>
        )}

        {/* All-time Stats */}
        <View className="px-5 mb-8">
          <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Histórico Total</Text>
          <View className="flex-row gap-3">
            <View className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <ArrowUpRight size={18} color="#10b981" />
              <Text className="text-slate-400 text-xs mt-2 mb-1">Total Ingresos</Text>
              <Text className="text-emerald-400 font-black text-base" numberOfLines={1}>
                {formatCurrency(allTimeStats.totalIncome, 'VES')}
              </Text>
            </View>
            <View className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl p-4">
              <TrendingDown size={18} color="#ef4444" />
              <Text className="text-slate-400 text-xs mt-2 mb-1">Total Gastos</Text>
              <Text className="text-red-400 font-black text-base" numberOfLines={1}>
                {formatCurrency(allTimeStats.totalExpenses, 'VES')}
              </Text>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
