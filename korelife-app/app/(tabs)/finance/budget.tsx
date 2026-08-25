import React from 'react';
import { View, Text, ScrollView, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Plus, PieChart, AlertCircle } from 'lucide-react-native';
import { useFinanceStore } from '../../../src/store/financeStore';
import { useRatesStore } from '../../../src/store/ratesStore';

export default function BudgetScreen() {
  const router = useRouter();
  const { budgets } = useFinanceStore();
  const { formatCurrency } = useRatesStore();

  const totalBudget = budgets.reduce((sum, b) => sum + b.amount, 0);
  const totalSpent = budgets.reduce((sum, b) => sum + b.spent, 0);
  const remaining = totalBudget - totalSpent;

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-4">
      <View className="flex-row items-center justify-between py-4 mb-4">
        <TouchableOpacity 
          onPress={() => router.back()} 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="p-2.5 rounded-2xl border"
        >
          <ArrowLeft size={20} color="#ffffff" />
        </TouchableOpacity>
        <Text style={{ color: '#ffffff' }} className="text-lg font-black">Presupuestos Mensuales</Text>
        <TouchableOpacity 
          style={{ backgroundColor: '#10b981' }}
          className="p-2.5 rounded-full shadow-lg"
        >
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Resumen */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
          className="rounded-3xl p-5 mb-5 shadow-xl"
        >
          <Text style={{ color: '#94a3b8' }} className="text-xs font-black uppercase mb-1">Total Presupuestado</Text>
          <Text style={{ color: '#ffffff' }} className="text-3xl font-black">{formatCurrency(totalBudget, 'VES')}</Text>
          
          <View className="flex-row justify-between mt-3 pt-3 border-t border-slate-800">
            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold">Gastado: {formatCurrency(totalSpent, 'VES')}</Text>
            <Text style={{ color: '#34d399' }} className="text-xs font-black">Disponible: {formatCurrency(remaining, 'VES')}</Text>
          </View>
        </View>

        {/* Categorías */}
        <Text style={{ color: '#ffffff' }} className="font-black text-base mb-3">Categorías Asignadas</Text>
        {budgets.map((b) => {
          const pct = Math.min(100, Math.round((b.spent / b.amount) * 100));
          const isWarning = pct >= 80;

          return (
            <View 
              key={b.id} 
              style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
              className="p-4 rounded-2xl mb-3 shadow-sm"
            >
              <View className="flex-row justify-between items-center mb-2">
                <Text style={{ color: '#ffffff' }} className="font-bold text-sm">{b.category}</Text>
                <Text style={{ color: '#cbd5e1' }} className="text-xs font-black">
                  {formatCurrency(b.spent, b.currency)} / {formatCurrency(b.amount, b.currency)}
                </Text>
              </View>

              <View style={{ backgroundColor: '#1e293b' }} className="w-full h-3 rounded-full overflow-hidden mb-2">
                <View 
                  style={{ width: `${pct}%`, backgroundColor: isWarning ? '#ef4444' : '#10b981' }} 
                  className="h-full rounded-full" 
                />
              </View>

              <Text style={{ color: isWarning ? '#f87171' : '#94a3b8' }} className="text-xs font-bold">
                {pct}% consumido este mes {isWarning ? '⚠️ Límite cercano' : '✅ En control'}
              </Text>
            </View>
          );
        })}
        <View className="h-10" />
      </ScrollView>
    </SafeAreaView>
  );
}