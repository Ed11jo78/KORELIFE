import React, { useState, useCallback, memo } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Modal, TextInput, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Target, Plus, Check, ChevronRight, Calendar, TrendingUp, X } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';
import { useRatesStore } from '../../../src/store/ratesStore';

const GOAL_COLORS = ['#10b981', '#6366f1', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];
const GOAL_EMOJIS = ['🎯', '🛡️', '🏖️', '📱', '🏠', '🚗', '📈', '🎓', '✈️', '💎'];

export default function GoalsScreen() {
  const goals = useTasksStore((s) => s.goals);
  const addGoal = useTasksStore((s) => s.addGoal);
  const updateGoalAmount = useTasksStore((s) => s.updateGoalAmount);
  const { formatCurrency } = useRatesStore();
  const [showModal, setShowModal] = useState(false);
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedGoal, setSelectedGoal] = useState<string | null>(null);
  const [depositAmount, setDepositAmount] = useState('');
  const [newGoal, setNewGoal] = useState({ title: '', emoji: '🎯', targetAmount: '', currency: 'USD' as const, deadline: '' });

  const handleDeposit = useCallback(() => {
    if (!selectedGoal || !depositAmount) return;
    const amount = parseFloat(depositAmount.replace(',', '.'));
    if (isNaN(amount) || amount <= 0) {
      Alert.alert('Monto inválido', 'Ingresa un monto válido mayor a 0.');
      return;
    }
    updateGoalAmount(selectedGoal, amount);
    setDepositAmount('');
    setShowModal(false);
    Alert.alert('¡Aporte registrado! 🎉', `Se añadieron ${formatCurrency(amount, 'USD')} a tu meta.`);
  }, [selectedGoal, depositAmount, updateGoalAmount, formatCurrency]);

  const handleCreateGoal = useCallback(() => {
    if (!newGoal.title.trim() || !newGoal.targetAmount) {
      Alert.alert('Datos requeridos', 'Completa el nombre y monto objetivo.');
      return;
    }
    addGoal({
      title: newGoal.title.trim(),
      emoji: newGoal.emoji,
      targetAmount: parseFloat(newGoal.targetAmount.replace(',', '.')),
      currentAmount: 0,
      currency: newGoal.currency,
      deadline: newGoal.deadline || '2026-12-31',
      color: GOAL_COLORS[Math.floor(Math.random() * GOAL_COLORS.length)],
    });
    setNewGoal({ title: '', emoji: '🎯', targetAmount: '', currency: 'USD', deadline: '' });
    setShowAddModal(false);
  }, [newGoal, addGoal]);

  const totalSaved = goals.reduce((s, g) => s + g.currentAmount, 0);
  const totalTarget = goals.reduce((s, g) => s + g.targetAmount, 0);
  const completedGoals = goals.filter((g) => g.currentAmount >= g.targetAmount).length;

  return (
    <SafeAreaView className="flex-1 bg-slate-950">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="px-5 pt-4 pb-6">
          <View className="flex-row justify-between items-center mb-5">
            <View>
              <Text className="text-slate-400 text-xs font-semibold uppercase tracking-widest">Finanzas</Text>
              <Text className="text-white text-2xl font-black">Metas 🎯</Text>
            </View>
            <TouchableOpacity
              onPress={() => setShowAddModal(true)}
              className="bg-emerald-500 w-10 h-10 rounded-full items-center justify-center"
            >
              <Plus size={22} color="#022c22" strokeWidth={2.5} />
            </TouchableOpacity>
          </View>

          {/* Stats */}
          <View className="bg-slate-900 rounded-2xl p-4 border border-slate-800 mb-2">
            <View className="flex-row justify-between mb-3">
              <View>
                <Text className="text-slate-400 text-xs mb-1">Ahorrado en total</Text>
                <Text className="text-emerald-400 text-2xl font-black">${totalSaved.toLocaleString('es-VE', { minimumFractionDigits: 0 })}</Text>
              </View>
              <View className="items-end">
                <Text className="text-slate-400 text-xs mb-1">Objetivo total</Text>
                <Text className="text-white text-2xl font-black">${totalTarget.toLocaleString('es-VE', { minimumFractionDigits: 0 })}</Text>
              </View>
            </View>
            <View className="h-2 bg-slate-800 rounded-full overflow-hidden mb-2">
              <View
                className="h-full bg-emerald-500 rounded-full"
                style={{ width: `${Math.min(100, (totalSaved / totalTarget) * 100)}%` }}
              />
            </View>
            <View className="flex-row justify-between">
              <Text className="text-slate-400 text-xs">{((totalSaved / totalTarget) * 100).toFixed(1)}% alcanzado</Text>
              <Text className="text-emerald-400 text-xs font-bold">{completedGoals} de {goals.length} completadas ✅</Text>
            </View>
          </View>
        </View>

        {/* Goals List */}
        <View className="px-5">
          <Text className="text-slate-300 text-sm font-bold mb-3">Tus Metas</Text>
          {goals.map((goal) => {
            const progress = Math.min(100, (goal.currentAmount / goal.targetAmount) * 100);
            const completed = goal.currentAmount >= goal.targetAmount;
            const remaining = goal.targetAmount - goal.currentAmount;

            return (
              <TouchableOpacity
                key={goal.id}
                onPress={() => { setSelectedGoal(goal.id); setShowModal(true); }}
                className="bg-slate-900 rounded-2xl p-4 mb-3 border border-slate-800"
              >
                <View className="flex-row items-center mb-3">
                  <View className="w-12 h-12 rounded-2xl items-center justify-center mr-3" style={{ backgroundColor: goal.color + '25' }}>
                    <Text className="text-2xl">{goal.emoji}</Text>
                  </View>
                  <View className="flex-1">
                    <View className="flex-row items-center justify-between">
                      <Text className="text-white font-bold text-base">{goal.title}</Text>
                      {completed && (
                        <View className="bg-emerald-500/20 px-2 py-0.5 rounded-full">
                          <Text className="text-emerald-400 text-xs font-bold">¡Meta! ✅</Text>
                        </View>
                      )}
                    </View>
                    <View className="flex-row items-center mt-0.5">
                      <Calendar size={12} color="#64748b" />
                      <Text className="text-slate-500 text-xs ml-1">{goal.deadline}</Text>
                    </View>
                  </View>
                </View>

                {/* Progress */}
                <View className="h-2.5 bg-slate-800 rounded-full overflow-hidden mb-2">
                  <View
                    className="h-full rounded-full"
                    style={{ width: `${progress}%`, backgroundColor: completed ? '#10b981' : goal.color }}
                  />
                </View>

                <View className="flex-row justify-between items-center">
                  <Text className="text-slate-300 text-sm font-semibold">
                    ${goal.currentAmount.toLocaleString('es-VE', { minimumFractionDigits: 0 })}
                    <Text className="text-slate-500 font-normal"> / ${goal.targetAmount.toLocaleString('es-VE', { minimumFractionDigits: 0 })} {goal.currency}</Text>
                  </Text>
                  {!completed ? (
                    <Text className="text-slate-400 text-xs">Faltan ${remaining.toLocaleString('es-VE', { minimumFractionDigits: 0 })}</Text>
                  ) : (
                    <Text className="text-emerald-400 text-xs font-bold">Completada 🎉</Text>
                  )}
                </View>
              </TouchableOpacity>
            );
          })}

          {/* Empty state */}
          {goals.length === 0 && (
            <View className="items-center py-12">
              <Text className="text-5xl mb-3">🎯</Text>
              <Text className="text-slate-400 text-base font-semibold">No tienes metas aún</Text>
              <Text className="text-slate-500 text-sm text-center mt-1">Crea tu primera meta financiera y empieza a ahorrar</Text>
            </View>
          )}

          {/* Add Button */}
          <TouchableOpacity
            onPress={() => setShowAddModal(true)}
            className="border-2 border-dashed border-slate-700 rounded-2xl py-5 items-center justify-center mb-8"
          >
            <Plus size={24} color="#64748b" />
            <Text className="text-slate-500 font-semibold mt-1.5">Agregar nueva meta</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>

      {/* Modal: Abonar */}
      <Modal visible={showModal} transparent animationType="slide" onRequestClose={() => setShowModal(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center mb-6">
              <Text className="text-white text-xl font-black">Abonar a Meta</Text>
              <TouchableOpacity onPress={() => setShowModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>
            {selectedGoal && (() => {
              const goal = goals.find(g => g.id === selectedGoal);
              if (!goal) return null;
              return (
                <View>
                  <View style={{ backgroundColor: '#1e293b' }} className="rounded-2xl p-4 mb-4 flex-row items-center border border-slate-700">
                    <Text className="text-2xl mr-3">{goal.emoji}</Text>
                    <View>
                      <Text className="text-white font-bold">{goal.title}</Text>
                      <Text className="text-slate-400 text-xs">
                        ${goal.currentAmount.toFixed(0)} / ${goal.targetAmount.toFixed(0)} {goal.currency}
                      </Text>
                    </View>
                  </View>
                  <Text className="text-slate-300 text-sm font-semibold mb-2">Monto a abonar ({goal.currency})</Text>
                  <View style={{ backgroundColor: '#1e293b', borderColor: '#334155' }} className="flex-row items-center rounded-2xl px-4 py-3 border mb-5">
                    <Text className="text-emerald-400 font-black text-xl mr-2">$</Text>
                    <TextInput
                      value={depositAmount}
                      onChangeText={setDepositAmount}
                      placeholder="0.00"
                      placeholderTextColor="#475569"
                      keyboardType="decimal-pad"
                      className="flex-1 text-white text-xl font-bold"
                    />
                  </View>
                  <TouchableOpacity onPress={handleDeposit} style={{ backgroundColor: '#10b981' }} className="py-4 rounded-2xl items-center shadow-lg">
                    <Text className="text-slate-950 font-black text-base">Registrar Aporte ✅</Text>
                  </TouchableOpacity>
                </View>
              );
            })()}
          </View>
        </View>
      </Modal>

      {/* Modal: Nueva Meta */}
      <Modal visible={showAddModal} transparent animationType="slide" onRequestClose={() => setShowAddModal(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center mb-5">
              <Text className="text-white text-xl font-black">Nueva Meta 🎯</Text>
              <TouchableOpacity onPress={() => setShowAddModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>

            {/* Emoji picker */}
            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Ícono</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
              {GOAL_EMOJIS.map((e) => (
                <TouchableOpacity
                  key={e}
                  onPress={() => setNewGoal(g => ({ ...g, emoji: e }))}
                  className={`w-11 h-11 rounded-xl items-center justify-center mr-2 ${newGoal.emoji === e ? 'bg-emerald-500/30 border border-emerald-500' : 'bg-slate-800'}`}
                >
                  <Text className="text-xl">{e}</Text>
                </TouchableOpacity>
              ))}
            </ScrollView>

            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Nombre de la meta</Text>
            <TextInput
              value={newGoal.title}
              onChangeText={(v) => setNewGoal(g => ({ ...g, title: v }))}
              placeholder="ej. Fondo de emergencia"
              placeholderTextColor="#475569"
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-4 border border-slate-700"
            />

            <View className="flex-row gap-3 mb-4">
              <View className="flex-1">
                <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Monto objetivo</Text>
                <TextInput
                  value={newGoal.targetAmount}
                  onChangeText={(v) => setNewGoal(g => ({ ...g, targetAmount: v }))}
                  placeholder="0.00"
                  placeholderTextColor="#475569"
                  keyboardType="decimal-pad"
                  className="bg-slate-800 text-white rounded-xl px-4 py-3 border border-slate-700"
                />
              </View>
              <View className="flex-1">
                <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Moneda</Text>
                <View className="flex-row">
                  {(['USD', 'VES', 'EUR'] as const).map((c) => (
                    <TouchableOpacity
                      key={c}
                      onPress={() => setNewGoal(g => ({ ...g, currency: c }))}
                      className={`flex-1 py-3 rounded-xl items-center mr-1 ${newGoal.currency === c ? 'bg-emerald-500' : 'bg-slate-800 border border-slate-700'}`}
                    >
                      <Text className={`text-xs font-bold ${newGoal.currency === c ? 'text-slate-950' : 'text-slate-400'}`}>{c}</Text>
                    </TouchableOpacity>
                  ))}
                </View>
              </View>
            </View>

            <TouchableOpacity onPress={handleCreateGoal} className="bg-emerald-500 py-4 rounded-2xl items-center">
              <Text className="text-slate-950 font-black text-base">Crear Meta 🎯</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}
