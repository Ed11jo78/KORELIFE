import React, { useState, useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Modal, TextInput, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Plus, Flame, Check, X } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';

const HABIT_EMOJIS = ['💧', '📖', '🏃', '💰', '🧘', '🥗', '😴', '✍️', '🎸', '🌿', '🚴', '💊'];

export default function HabitsScreen() {
  const router = useRouter();
  const habits = useTasksStore((s) => s.habits);
  const toggleHabit = useTasksStore((s) => s.toggleHabit);
  const addHabit = useTasksStore((s) => s.addHabit);
  const [showModal, setShowModal] = useState(false);
  const [newHabit, setNewHabit] = useState({ name: '', emoji: '💧', frequency: 'daily' as const });

  const handleCreate = useCallback(() => {
    if (!newHabit.name.trim()) {
      Alert.alert('Nombre requerido', 'Ingresa el nombre del hábito.');
      return;
    }
    addHabit({
      user_id: '1',
      name: newHabit.name.trim(),
      streak: 0,
      completed_today: false,
      frequency: newHabit.frequency,
    });
    setNewHabit({ name: '', emoji: '💧', frequency: 'daily' });
    setShowModal(false);
  }, [newHabit, addHabit]);

  const completedCount = habits.filter((h) => h.completed_today).length;
  const totalStreak = habits.reduce((s, h) => s + h.streak, 0);

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-4">
      {/* Header */}
      <View className="flex-row items-center justify-between py-4 mb-4">
        <TouchableOpacity onPress={() => router.back()} className="p-2 bg-slate-900 rounded-full border border-slate-800">
          <ArrowLeft size={20} color="white" />
        </TouchableOpacity>
        <Text className="text-white text-lg font-bold">Hábitos Diarios</Text>
        <TouchableOpacity onPress={() => setShowModal(true)} className="p-2 bg-emerald-500 rounded-full">
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Stats */}
        <View className="flex-row gap-3 mb-5">
          <View className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl p-4">
            <Text className="text-slate-400 text-xs mb-1">Hoy completados</Text>
            <Text className="text-white text-2xl font-black">{completedCount}<Text className="text-slate-500 text-base">/{habits.length}</Text></Text>
          </View>
          <View className="flex-1 bg-amber-500/10 border border-amber-500/20 rounded-2xl p-4">
            <View className="flex-row items-center mb-1">
              <Flame size={14} color="#f59e0b" />
              <Text className="text-amber-400 text-xs ml-1">Racha total</Text>
            </View>
            <Text className="text-amber-400 text-2xl font-black">{totalStreak}<Text className="text-amber-500/60 text-base"> días</Text></Text>
          </View>
        </View>

        {/* Progress Bar */}
        {habits.length > 0 && (
          <View className="bg-slate-900 border border-slate-800 rounded-2xl p-4 mb-5">
            <View className="flex-row justify-between mb-2">
              <Text className="text-slate-300 text-sm font-semibold">Progreso de hoy</Text>
              <Text className="text-emerald-400 text-sm font-bold">{((completedCount / habits.length) * 100).toFixed(0)}%</Text>
            </View>
            <View className="h-3 bg-slate-800 rounded-full overflow-hidden">
              <View
                className="h-full bg-emerald-500 rounded-full"
                style={{ width: `${(completedCount / habits.length) * 100}%` }}
              />
            </View>
          </View>
        )}

        {/* Habits List */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-3">Tus Hábitos</Text>
        {habits.map((h) => (
          <TouchableOpacity
            key={h.id}
            onPress={() => toggleHabit(h.id)}
            className={`p-4 rounded-2xl mb-3 border flex-row items-center justify-between ${
              h.completed_today ? 'bg-emerald-950/40 border-emerald-500/50' : 'bg-slate-900 border-slate-800'
            }`}
          >
            <View className="flex-row items-center flex-1 mr-3">
              <Text className="text-2xl mr-3">{h.name.includes('agua') || h.name.includes('Agua') ? '💧' : h.name.includes('Leer') || h.name.includes('libro') ? '📖' : h.name.includes('Ejercicio') || h.name.includes('30') ? '🏃' : h.name.includes('gasto') || h.name.includes('Registrar') ? '💰' : h.name.includes('Meditar') ? '🧘' : '⭐'}</Text>
              <View className="flex-1">
                <Text className={`font-bold text-sm ${h.completed_today ? 'text-emerald-300 line-through' : 'text-white'}`}>
                  {h.name}
                </Text>
                <View className="flex-row items-center mt-0.5">
                  <Flame size={12} color="#f59e0b" />
                  <Text className="text-amber-400 text-xs font-semibold ml-1">
                    {h.streak} días de racha {h.streak >= 7 ? '🔥' : ''}
                  </Text>
                </View>
              </View>
            </View>

            <View className={`w-9 h-9 rounded-full items-center justify-center border ${
              h.completed_today ? 'bg-emerald-500 border-emerald-400' : 'bg-slate-800 border-slate-700'
            }`}>
              {h.completed_today && <Check size={18} color="#022c22" strokeWidth={2.5} />}
            </View>
          </TouchableOpacity>
        ))}

        {habits.length === 0 && (
          <View className="items-center py-12">
            <Text className="text-5xl mb-3">🌱</Text>
            <Text className="text-slate-400 font-semibold">Sin hábitos aún</Text>
            <Text className="text-slate-500 text-sm text-center mt-1">Agrega tu primer hábito y empieza a construir tu mejor versión</Text>
          </View>
        )}

        <View className="h-8" />
      </ScrollView>

      {/* Create Habit Modal */}
      <Modal visible={showModal} transparent animationType="slide" onRequestClose={() => setShowModal(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center mb-5">
              <Text className="text-white text-xl font-black">Nuevo Hábito 🌱</Text>
              <TouchableOpacity onPress={() => setShowModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>

            {/* Emoji picker */}
            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Ícono</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
              {HABIT_EMOJIS.map((e) => (
                <TouchableOpacity
                  key={e}
                  onPress={() => setNewHabit((h) => ({ ...h, emoji: e }))}
                  className={`w-11 h-11 rounded-xl items-center justify-center mr-2 ${newHabit.emoji === e ? 'bg-emerald-500/30 border border-emerald-500' : 'bg-slate-800'}`}
                >
                  <Text className="text-xl">{e}</Text>
                </TouchableOpacity>
              ))}
            </ScrollView>

            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Nombre del hábito</Text>
            <TextInput
              value={newHabit.name}
              onChangeText={(v) => setNewHabit((h) => ({ ...h, name: v }))}
              placeholder="ej. Beber 2 litros de agua"
              placeholderTextColor="#475569"
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-4 border border-slate-700"
            />

            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Frecuencia</Text>
            <View className="flex-row gap-3 mb-5">
              {(['daily', 'weekly'] as const).map((f) => (
                <TouchableOpacity
                  key={f}
                  onPress={() => setNewHabit((h) => ({ ...h, frequency: f }))}
                  className={`flex-1 py-3 rounded-xl items-center ${newHabit.frequency === f ? 'bg-emerald-500' : 'bg-slate-800 border border-slate-700'}`}
                >
                  <Text className={`font-bold ${newHabit.frequency === f ? 'text-slate-950' : 'text-slate-400'}`}>
                    {f === 'daily' ? 'Diario' : 'Semanal'}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>

            <TouchableOpacity onPress={handleCreate} className="bg-emerald-500 py-4 rounded-2xl items-center">
              <Text className="text-slate-950 font-black text-base">Crear Hábito 🌱</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}