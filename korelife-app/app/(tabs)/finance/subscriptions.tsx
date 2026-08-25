import React, { useState, useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Switch, Modal, Alert, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { CreditCard, Plus, AlertCircle, X, Calendar, ToggleLeft } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';
import { useRatesStore } from '../../../src/store/ratesStore';

const PERIOD_LABELS: Record<string, string> = {
  monthly: 'Mensual',
  yearly: 'Anual',
  weekly: 'Semanal',
};

export default function SubscriptionsScreen() {
  const subscriptions = useTasksStore((s) => s.subscriptions);
  const toggleSubscription = useTasksStore((s) => s.toggleSubscription);
  const { formatCurrency, convert } = useRatesStore();
  const [showAdd, setShowAdd] = useState(false);

  const activeSubscriptions = subscriptions.filter((s) => s.active);
  const monthlyUSD = activeSubscriptions.reduce((sum, s) => {
    let amount = s.amount;
    if (s.period === 'yearly') amount = s.amount / 12;
    if (s.period === 'weekly') amount = s.amount * 4.33;
    // normalize to USD
    const inUsd = s.currency === 'USD' ? amount : convert(amount, s.currency, 'USD');
    return sum + inUsd;
  }, 0);

  const { rates, selectedProvider } = useRatesStore();
  const currentRate = rates[selectedProvider]?.rate || 36.54;
  const monthlyVES = monthlyUSD * currentRate;

  // Days until billing
  const getDaysUntil = (dateStr: string) => {
    const today = new Date();
    const target = new Date(dateStr);
    const diff = Math.ceil((target.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    return diff;
  };

  const upcomingBilling = subscriptions
    .filter((s) => s.active)
    .filter((s) => getDaysUntil(s.nextBilling) <= 7)
    .sort((a, b) => getDaysUntil(a.nextBilling) - getDaysUntil(b.nextBilling));

  return (
    <SafeAreaView className="flex-1 bg-slate-950">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="px-5 pt-4 pb-2">
          <View className="flex-row justify-between items-center mb-5">
            <View>
              <Text className="text-slate-400 text-xs font-semibold uppercase tracking-widest">Finanzas</Text>
              <Text className="text-white text-2xl font-black">Suscripciones 📋</Text>
            </View>
            <TouchableOpacity
              onPress={() => setShowAdd(true)}
              className="bg-indigo-500 w-10 h-10 rounded-full items-center justify-center"
            >
              <Plus size={22} color="white" strokeWidth={2.5} />
            </TouchableOpacity>
          </View>

          {/* Summary Card */}
          <View className="bg-gradient-to-br from-indigo-900 to-slate-900 bg-indigo-900 rounded-2xl p-5 mb-4 border border-indigo-800">
            <Text className="text-indigo-300 text-xs font-semibold uppercase mb-1">Gasto Fijo Mensual</Text>
            <Text className="text-white text-3xl font-black mb-0.5">$ {monthlyUSD.toFixed(2)}</Text>
            <Text className="text-indigo-300 text-sm">≈ Bs. {monthlyVES.toLocaleString('es-VE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}</Text>
            <View className="flex-row mt-3 gap-4">
              <View>
                <Text className="text-indigo-400 text-xs">Activas</Text>
                <Text className="text-white font-bold">{activeSubscriptions.length}</Text>
              </View>
              <View>
                <Text className="text-indigo-400 text-xs">Pausadas</Text>
                <Text className="text-white font-bold">{subscriptions.length - activeSubscriptions.length}</Text>
              </View>
              <View>
                <Text className="text-indigo-400 text-xs">Total</Text>
                <Text className="text-white font-bold">{subscriptions.length}</Text>
              </View>
            </View>
          </View>

          {/* Upcoming Billing Alert */}
          {upcomingBilling.length > 0 && (
            <View className="bg-amber-500/10 border border-amber-500/30 rounded-xl p-3 mb-5 flex-row items-start">
              <AlertCircle size={18} color="#f59e0b" className="mt-0.5 mr-2" />
              <View className="flex-1 ml-2">
                <Text className="text-amber-400 font-bold text-sm">Cobros próximos</Text>
                {upcomingBilling.map((s) => (
                  <Text key={s.id} className="text-amber-300 text-xs mt-0.5">
                    {s.emoji} {s.name} — en {getDaysUntil(s.nextBilling)} días (${s.amount})
                  </Text>
                ))}
              </View>
            </View>
          )}
        </View>

        {/* Subscriptions List */}
        <View className="px-5">
          <Text className="text-slate-300 text-sm font-bold mb-3">Todas las Suscripciones</Text>
          <View className="bg-slate-900 rounded-2xl border border-slate-800 overflow-hidden mb-8">
            {subscriptions.map((sub, index) => {
              const daysUntil = getDaysUntil(sub.nextBilling);
              const isUrgent = daysUntil <= 3 && sub.active;

              return (
                <View
                  key={sub.id}
                  className={`flex-row items-center p-4 ${index < subscriptions.length - 1 ? 'border-b border-slate-800' : ''}`}
                >
                  {/* Icon */}
                  <View className={`w-12 h-12 rounded-xl items-center justify-center mr-3 ${sub.active ? 'bg-slate-800' : 'bg-slate-800/50'}`}>
                    <Text className="text-2xl">{sub.emoji}</Text>
                  </View>

                  {/* Info */}
                  <View className="flex-1">
                    <Text className={`font-bold text-sm ${sub.active ? 'text-white' : 'text-slate-500'}`}>{sub.name}</Text>
                    <View className="flex-row items-center mt-0.5">
                      <Calendar size={11} color={isUrgent ? '#f59e0b' : '#64748b'} />
                      <Text className={`text-xs ml-1 ${isUrgent ? 'text-amber-400' : 'text-slate-500'}`}>
                        {sub.active ? `Próximo cobro: ${sub.nextBilling}` : 'Pausada'}
                      </Text>
                    </View>
                  </View>

                  {/* Amount */}
                  <View className="items-end mr-3">
                    <Text className={`font-black text-sm ${sub.active ? 'text-white' : 'text-slate-600'}`}>
                      ${sub.amount}
                    </Text>
                    <Text className="text-slate-500 text-xs">{PERIOD_LABELS[sub.period]}</Text>
                  </View>

                  {/* Toggle */}
                  <Switch
                    value={sub.active}
                    onValueChange={() => toggleSubscription(sub.id)}
                    trackColor={{ false: '#334155', true: '#059669' }}
                    thumbColor={sub.active ? '#10b981' : '#64748b'}
                  />
                </View>
              );
            })}
          </View>
        </View>
      </ScrollView>

      {/* Add Modal (simple alert for now) */}
      <Modal visible={showAdd} transparent animationType="slide" onRequestClose={() => setShowAdd(false)}>
        <View className="flex-1 justify-end bg-black/60">
          <View className="bg-slate-900 rounded-t-3xl p-6 border-t border-slate-800">
            <View className="flex-row justify-between items-center mb-5">
              <Text className="text-white text-xl font-black">Nueva Suscripción</Text>
              <TouchableOpacity onPress={() => setShowAdd(false)} className="p-2 bg-slate-800 rounded-full">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>
            <Text className="text-slate-400 text-center py-6">Próximamente podrás agregar suscripciones personalizadas con recordatorios automáticos. 🔔</Text>
            <TouchableOpacity onPress={() => setShowAdd(false)} className="bg-indigo-500 py-4 rounded-2xl items-center">
              <Text className="text-white font-black">Entendido</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}
