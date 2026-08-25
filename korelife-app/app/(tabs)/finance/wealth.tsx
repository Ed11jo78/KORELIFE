import React, { useMemo } from 'react';
import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { TrendingUp, TrendingDown, Landmark, Banknote, CreditCard, DollarSign } from 'lucide-react-native';
import { useFinanceStore } from '../../../src/store/financeStore';
import { useRatesStore } from '../../../src/store/ratesStore';

type AssetItem = {
  name: string;
  amount: number;
  currency: 'VES' | 'USD' | 'EUR';
  icon: React.ReactNode;
  color: string;
};

export default function WealthScreen() {
  const accounts = useFinanceStore((s) => s.accounts);
  const { convert, formatCurrency, rates, selectedProvider } = useRatesStore();

  const currentRate = rates[selectedProvider]?.rate || 36.54;
  const eurRate = rates['bcv_eur']?.rate || 39.85;

  // Calculate totals in VES
  const totalAssetsVes = useMemo(() => {
    return accounts
      .filter((a) => a.type !== 'credit')
      .reduce((sum, acc) => sum + convert(acc.balance, acc.currency, 'VES'), 0);
  }, [accounts, convert]);

  const totalLiabilitiesVes = useMemo(() => {
    return accounts
      .filter((a) => a.type === 'credit')
      .reduce((sum, acc) => sum + convert(acc.balance, acc.currency, 'VES'), 0);
  }, [accounts, convert]);

  const netWorthVes = totalAssetsVes - totalLiabilitiesVes;
  const netWorthUsd = netWorthVes / currentRate;

  const bankAccountsTotal = useMemo(() => {
    return accounts.filter(a => a.type === 'bank').reduce((s, a) => s + convert(a.balance, a.currency, 'VES'), 0);
  }, [accounts, convert]);

  const cashTotal = useMemo(() => {
    return accounts.filter(a => a.type === 'cash').reduce((s, a) => s + convert(a.balance, a.currency, 'VES'), 0);
  }, [accounts, convert]);

  const investmentsTotal = useMemo(() => {
    return accounts.filter(a => a.type === 'investment').reduce((s, a) => s + convert(a.balance, a.currency, 'VES'), 0);
  }, [accounts, convert]);

  const TYPE_LABELS: Record<string, string> = {
    bank: 'Banco',
    cash: 'Efectivo',
    investment: 'Inversión',
    credit: 'Crédito',
  };

  const assetCategories = [
    { label: 'Cuentas Bancarias', amount: bankAccountsTotal, icon: <Landmark size={20} color="#6366f1" />, bg: 'bg-indigo-500/15', border: 'border-indigo-500/20' },
    { label: 'Efectivo', amount: cashTotal, icon: <Banknote size={20} color="#10b981" />, bg: 'bg-emerald-500/15', border: 'border-emerald-500/20' },
    { label: 'Inversiones', amount: investmentsTotal, icon: <TrendingUp size={20} color="#f59e0b" />, bg: 'bg-amber-500/15', border: 'border-amber-500/20' },
  ];

  const assetPct = totalAssetsVes > 0 ? [
    (bankAccountsTotal / totalAssetsVes) * 100,
    (cashTotal / totalAssetsVes) * 100,
    (investmentsTotal / totalAssetsVes) * 100,
  ] : [33, 33, 33];

  return (
    <SafeAreaView className="flex-1 bg-slate-950">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="px-5 pt-4 pb-6">
          <Text className="text-slate-400 text-xs font-semibold uppercase tracking-widest">Finanzas</Text>
          <Text className="text-white text-2xl font-black">Patrimonio 💎</Text>
        </View>

        {/* Net Worth Card */}
        <View className="px-5 mb-6">
          <View className="bg-slate-900 border border-slate-800 rounded-2xl p-5">
            <Text className="text-slate-400 text-xs font-semibold uppercase">Patrimonio Neto</Text>
            <Text className="text-white text-4xl font-black mt-1" numberOfLines={1} adjustsFontSizeToFit>
              {formatCurrency(netWorthVes, 'VES')}
            </Text>
            <Text className="text-slate-400 text-sm mt-1">≈ $ {netWorthUsd.toLocaleString('es-VE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</Text>

            {/* Mini bar */}
            <View className="mt-4">
              <View className="flex-row h-3 rounded-full overflow-hidden bg-slate-800">
                <View className="bg-emerald-500 h-full" style={{ width: `${Math.min(100, (totalAssetsVes / (totalAssetsVes + totalLiabilitiesVes || 1)) * 100)}%` }} />
                <View className="bg-red-500/60 h-full flex-1" />
              </View>
              <View className="flex-row justify-between mt-2">
                <View className="flex-row items-center">
                  <View className="w-2 h-2 rounded-full bg-emerald-500 mr-1.5" />
                  <Text className="text-slate-400 text-xs">Activos: {formatCurrency(totalAssetsVes, 'VES')}</Text>
                </View>
                <View className="flex-row items-center">
                  <View className="w-2 h-2 rounded-full bg-red-500/60 mr-1.5" />
                  <Text className="text-slate-400 text-xs">Pasivos: {formatCurrency(totalLiabilitiesVes, 'VES')}</Text>
                </View>
              </View>
            </View>
          </View>
        </View>

        {/* Asset Categories */}
        <View className="px-5 mb-6">
          <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Distribución de Activos</Text>
          <View className="flex-row gap-2">
            {assetCategories.map((cat, i) => (
              <View key={cat.label} className={`flex-1 ${cat.bg} border ${cat.border} rounded-2xl p-3`}>
                {cat.icon}
                <Text className="text-slate-400 text-xs mt-2 mb-1" numberOfLines={2}>{cat.label}</Text>
                <Text className="text-white font-bold text-xs" numberOfLines={1}>{formatCurrency(cat.amount, 'VES')}</Text>
                <Text className="text-slate-500 text-xs">{assetPct[i].toFixed(0)}%</Text>
              </View>
            ))}
          </View>
        </View>

        {/* Accounts — Assets */}
        <View className="px-5 mb-6">
          <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Cuentas y Activos</Text>
          <View className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
            {accounts.filter((a) => a.type !== 'credit').map((acc, index, arr) => {
              const inVes = convert(acc.balance, acc.currency, 'VES');
              const pct = totalAssetsVes > 0 ? (inVes / totalAssetsVes) * 100 : 0;
              return (
                <View key={acc.id} className={`px-4 py-3.5 ${index < arr.length - 1 ? 'border-b border-slate-800' : ''}`}>
                  <View className="flex-row justify-between items-start">
                    <View className="flex-1 mr-3">
                      <Text className="text-white font-semibold text-sm">{acc.name}</Text>
                      <View className="flex-row items-center mt-0.5">
                        <View className="bg-slate-700 px-1.5 py-0.5 rounded-md mr-1.5">
                          <Text className="text-slate-400 text-xs">{TYPE_LABELS[acc.type]}</Text>
                        </View>
                        <Text className="text-slate-500 text-xs">{acc.currency}</Text>
                      </View>
                    </View>
                    <View className="items-end">
                      <Text className="text-emerald-400 font-black text-sm">
                        {acc.currency === 'VES' ? formatCurrency(acc.balance, 'VES') : `$ ${acc.balance.toLocaleString('es-VE', { minimumFractionDigits: 2 })}`}
                      </Text>
                      {acc.currency !== 'VES' && (
                        <Text className="text-slate-500 text-xs">{formatCurrency(inVes, 'VES')}</Text>
                      )}
                    </View>
                  </View>
                  <View className="h-1 bg-slate-800 rounded-full mt-2 overflow-hidden">
                    <View className="h-full bg-emerald-500/60 rounded-full" style={{ width: `${pct}%` }} />
                  </View>
                </View>
              );
            })}
          </View>
        </View>

        {/* Liabilities */}
        {accounts.filter((a) => a.type === 'credit').length > 0 && (
          <View className="px-5 mb-8">
            <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Pasivos y Créditos</Text>
            <View className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden">
              {accounts.filter((a) => a.type === 'credit').map((acc, index, arr) => (
                <View key={acc.id} className={`px-4 py-3.5 flex-row justify-between ${index < arr.length - 1 ? 'border-b border-slate-800' : ''}`}>
                  <View>
                    <Text className="text-white font-semibold text-sm">{acc.name}</Text>
                    <Text className="text-slate-500 text-xs">Crédito / {acc.currency}</Text>
                  </View>
                  <Text className="text-red-400 font-black text-sm">
                    -{acc.currency === 'VES' ? formatCurrency(acc.balance, 'VES') : `$ ${acc.balance.toFixed(2)}`}
                  </Text>
                </View>
              ))}
            </View>
          </View>
        )}

        {/* No liabilities */}
        {accounts.filter((a) => a.type === 'credit').length === 0 && (
          <View className="px-5 mb-8">
            <View className="bg-emerald-500/10 border border-emerald-500/20 rounded-2xl p-5 items-center">
              <Text className="text-3xl mb-2">🎉</Text>
              <Text className="text-emerald-400 font-bold text-center">¡Sin deudas registradas!</Text>
              <Text className="text-slate-500 text-xs text-center mt-1">Mantén tus finanzas libres de pasivos para maximizar tu patrimonio.</Text>
            </View>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}
