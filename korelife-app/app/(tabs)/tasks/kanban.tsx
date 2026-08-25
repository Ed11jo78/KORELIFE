import React, { useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Plus, CheckCircle2, Circle, Clock, AlertTriangle, LayoutDashboard } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';

const PRIORITY_COLORS: Record<string, string> = { high: '#ef4444', medium: '#f59e0b', low: '#10b981' };
const PRIORITY_LABELS: Record<string, string> = { high: 'Alta', medium: 'Media', low: 'Baja' };

export default function KanbanScreen() {
  const router = useRouter();
  const tasks = useTasksStore((s) => s.tasks);
  const toggleTask = useTasksStore((s) => s.toggleTask);
  const deleteTask = useTasksStore((s) => s.deleteTask);

  const todo = tasks.filter((t) => !t.completed);
  const done = tasks.filter((t) => t.completed);
  const highPriority = todo.filter((t) => t.priority === 'high');

  const handleLongPress = useCallback((id: string, title: string) => {
    Alert.alert('Opciones', title, [
      { text: 'Cancelar', style: 'cancel' },
      { text: 'Eliminar', style: 'destructive', onPress: () => deleteTask(id) },
    ]);
  }, [deleteTask]);

  const renderCard = (t: (typeof tasks)[0]) => (
    <TouchableOpacity
      key={t.id}
      onPress={() => toggleTask(t.id)}
      onLongPress={() => handleLongPress(t.id, t.title)}
      className="bg-slate-800/80 p-3.5 rounded-2xl mb-2.5 border border-slate-700/60 active:bg-slate-700"
    >
      <View className="flex-row items-start justify-between mb-1.5">
        <Text className={`text-sm font-semibold flex-1 mr-2 ${t.completed ? 'line-through text-slate-500' : 'text-white'}`}>
          {t.title}
        </Text>
        {t.completed ? (
          <CheckCircle2 size={16} color="#10b981" />
        ) : (
          <Circle size={16} color="#64748b" />
        )}
      </View>
      <View className="flex-row items-center justify-between">
        <View className="flex-row items-center">
          <View className="w-2 h-2 rounded-full mr-1.5" style={{ backgroundColor: PRIORITY_COLORS[t.priority] }} />
          <Text className="text-xs font-semibold" style={{ color: PRIORITY_COLORS[t.priority] }}>
            {PRIORITY_LABELS[t.priority]}
          </Text>
        </View>
        {t.due_date && (
          <Text className="text-slate-500 text-[10px]">📅 {t.due_date}</Text>
        )}
      </View>
    </TouchableOpacity>
  );

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-4">
      {/* Header */}
      <View className="flex-row items-center justify-between py-4 mb-4">
        <TouchableOpacity onPress={() => router.back()} className="p-2 bg-slate-900 rounded-full border border-slate-800">
          <ArrowLeft size={20} color="white" />
        </TouchableOpacity>
        <View className="flex-row items-center gap-2">
          <LayoutDashboard size={18} color="#6366f1" />
          <Text className="text-white text-lg font-bold ml-1">Tablero Kanban</Text>
        </View>
        <View className="bg-indigo-500/20 px-3 py-1 rounded-full border border-indigo-500/30">
          <Text className="text-indigo-400 text-xs font-bold">{tasks.length} total</Text>
        </View>
      </View>

      {/* Stats Row */}
      <View className="flex-row gap-2 mb-4">
        <View className="flex-1 bg-amber-500/10 border border-amber-500/20 rounded-xl p-2.5 items-center">
          <Text className="text-amber-400 font-black text-lg">{todo.length}</Text>
          <Text className="text-amber-400/70 text-[10px]">Pendientes</Text>
        </View>
        <View className="flex-1 bg-red-500/10 border border-red-500/20 rounded-xl p-2.5 items-center">
          <Text className="text-red-400 font-black text-lg">{highPriority.length}</Text>
          <Text className="text-red-400/70 text-[10px]">Urgentes</Text>
        </View>
        <View className="flex-1 bg-emerald-500/10 border border-emerald-500/20 rounded-xl p-2.5 items-center">
          <Text className="text-emerald-400 font-black text-lg">{done.length}</Text>
          <Text className="text-emerald-400/70 text-[10px]">Completadas</Text>
        </View>
      </View>

      {/* Kanban Board */}
      <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-1 -mx-1">
        {/* Column: Por Hacer */}
        <View className="w-72 bg-slate-900 rounded-3xl p-4 mr-3 border border-slate-800 flex-1 mx-1" style={{ minHeight: 200 }}>
          <View className="flex-row justify-between items-center mb-3">
            <View className="flex-row items-center">
              <Clock size={16} color="#f59e0b" />
              <Text className="text-white font-bold text-sm ml-2">Por Hacer</Text>
            </View>
            <View className="bg-amber-500/20 px-2 py-0.5 rounded-full">
              <Text className="text-amber-400 text-xs font-bold">{todo.length}</Text>
            </View>
          </View>
          <ScrollView showsVerticalScrollIndicator={false}>
            {todo.length === 0 ? (
              <View className="items-center py-8">
                <Text className="text-4xl mb-2">🎉</Text>
                <Text className="text-slate-500 text-xs text-center">¡Sin tareas pendientes!</Text>
              </View>
            ) : (
              todo.map(renderCard)
            )}
          </ScrollView>
        </View>

        {/* Column: Urgentes */}
        <View className="w-72 bg-slate-900 rounded-3xl p-4 mr-3 border border-red-900/40 flex-1 mx-1" style={{ minHeight: 200 }}>
          <View className="flex-row justify-between items-center mb-3">
            <View className="flex-row items-center">
              <AlertTriangle size={16} color="#ef4444" />
              <Text className="text-white font-bold text-sm ml-2">Urgentes</Text>
            </View>
            <View className="bg-red-500/20 px-2 py-0.5 rounded-full">
              <Text className="text-red-400 text-xs font-bold">{highPriority.length}</Text>
            </View>
          </View>
          <ScrollView showsVerticalScrollIndicator={false}>
            {highPriority.length === 0 ? (
              <View className="items-center py-8">
                <Text className="text-4xl mb-2">✅</Text>
                <Text className="text-slate-500 text-xs text-center">Sin urgentes</Text>
              </View>
            ) : (
              highPriority.map(renderCard)
            )}
          </ScrollView>
        </View>

        {/* Column: Completadas */}
        <View className="w-72 bg-slate-900 rounded-3xl p-4 border border-slate-800 flex-1 mx-1" style={{ minHeight: 200 }}>
          <View className="flex-row justify-between items-center mb-3">
            <View className="flex-row items-center">
              <CheckCircle2 size={16} color="#10b981" />
              <Text className="text-white font-bold text-sm ml-2">Completadas</Text>
            </View>
            <View className="bg-emerald-500/20 px-2 py-0.5 rounded-full">
              <Text className="text-emerald-400 text-xs font-bold">{done.length}</Text>
            </View>
          </View>
          <ScrollView showsVerticalScrollIndicator={false}>
            {done.length === 0 ? (
              <View className="items-center py-8">
                <Text className="text-4xl mb-2">📋</Text>
                <Text className="text-slate-500 text-xs text-center">Aún no hay completadas</Text>
              </View>
            ) : (
              done.map(renderCard)
            )}
          </ScrollView>
        </View>
      </ScrollView>

      <Text className="text-slate-600 text-xs text-center py-2">Toca para marcar · Mantén para eliminar</Text>
    </SafeAreaView>
  );
}