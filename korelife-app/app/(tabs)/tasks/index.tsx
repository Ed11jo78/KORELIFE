import React, { useState, useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { CheckCircle2, Circle, Plus, AlertCircle } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';

const PRIORITY_LABELS: Record<string, string> = { high: 'Alta', medium: 'Media', low: 'Baja' };
const PRIORITY_COLORS: Record<string, string> = { high: '#ef4444', medium: '#f59e0b', low: '#64748b' };

export default function TasksScreen() {
  const tasks = useTasksStore((s) => s.tasks);
  const addTask = useTasksStore((s) => s.addTask);
  const toggleTask = useTasksStore((s) => s.toggleTask);
  const deleteTask = useTasksStore((s) => s.deleteTask);
  const [newTitle, setNewTitle] = useState('');
  const [priority, setPriority] = useState<'low' | 'medium' | 'high'>('medium');
  const [filter, setFilter] = useState<'all' | 'pending' | 'completed'>('all');

  const handleAdd = useCallback(() => {
    if (!newTitle.trim()) return;
    addTask({ user_id: '1', title: newTitle.trim(), completed: false, priority });
    setNewTitle('');
  }, [newTitle, priority, addTask]);

  const filtered = tasks.filter((t) => {
    if (filter === 'pending') return !t.completed;
    if (filter === 'completed') return t.completed;
    return true;
  });

  const completedCount = tasks.filter((t) => t.completed).length;

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-4">
      {/* Header */}
      <View className="py-4 flex-row justify-between items-center mb-2">
        <View>
          <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider">Productividad</Text>
          <Text className="text-white text-2xl font-black">Mis Tareas</Text>
        </View>
        <View className="bg-emerald-500/20 px-3 py-1.5 rounded-full border border-emerald-500/30">
          <Text className="text-emerald-400 text-xs font-bold">
            {completedCount}/{tasks.length} listas ✅
          </Text>
        </View>
      </View>

      {/* Quick Add */}
      <View className="bg-slate-900 rounded-2xl border border-slate-800 mb-3 overflow-hidden">
        <View className="flex-row items-center p-2 pr-3">
          <TextInput
            value={newTitle}
            onChangeText={setNewTitle}
            placeholder="¿Qué necesitas hacer hoy?..."
            placeholderTextColor="#64748b"
            className="flex-1 px-3 py-2 text-white text-sm"
            onSubmitEditing={handleAdd}
            returnKeyType="done"
          />
          <TouchableOpacity
            onPress={handleAdd}
            className="bg-emerald-500 p-2.5 rounded-xl active:bg-emerald-600"
          >
            <Plus size={18} color="#022c22" strokeWidth={2.5} />
          </TouchableOpacity>
        </View>
        {/* Priority selector */}
        <View className="flex-row border-t border-slate-800 px-2 py-1.5 gap-2">
          {(['low', 'medium', 'high'] as const).map((p) => (
            <TouchableOpacity
              key={p}
              onPress={() => setPriority(p)}
              className={`px-3 py-1 rounded-lg flex-row items-center ${priority === p ? 'bg-slate-700' : ''}`}
            >
              <View className="w-2 h-2 rounded-full mr-1.5" style={{ backgroundColor: PRIORITY_COLORS[p] }} />
              <Text className={`text-xs font-semibold ${priority === p ? 'text-white' : 'text-slate-500'}`}>
                {PRIORITY_LABELS[p]}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Filters */}
      <View className="flex-row gap-2 mb-4">
        {(['all', 'pending', 'completed'] as const).map((f) => (
          <TouchableOpacity
            key={f}
            onPress={() => setFilter(f)}
            className={`px-4 py-1.5 rounded-xl border ${
              filter === f ? 'bg-emerald-950/60 border-emerald-500' : 'bg-slate-900 border-slate-800'
            }`}
          >
            <Text className={`text-xs font-semibold ${filter === f ? 'text-emerald-400' : 'text-slate-400'}`}>
              {f === 'all' ? 'Todas' : f === 'pending' ? 'Pendientes' : 'Completadas'}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* Tasks List */}
      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        {filtered.length === 0 ? (
          <View className="items-center py-16">
            <AlertCircle size={40} color="#334155" />
            <Text className="text-slate-500 text-sm font-medium mt-3">
              {filter === 'completed' ? 'No has completado tareas aún.' : 'No hay tareas en esta sección.'}
            </Text>
          </View>
        ) : (
          filtered.map((task) => (
            <TouchableOpacity
              key={task.id}
              onPress={() => toggleTask(task.id)}
              onLongPress={() => deleteTask(task.id)}
              className="bg-slate-900 p-4 rounded-2xl mb-2.5 flex-row items-center justify-between border border-slate-800/80 active:bg-slate-800"
            >
              <View className="flex-row items-center flex-1 mr-3">
                {task.completed ? (
                  <CheckCircle2 size={22} color="#10b981" />
                ) : (
                  <Circle size={22} color="#475569" />
                )}
                <View className="ml-3 flex-1">
                  <Text className={`text-sm font-medium ${task.completed ? 'line-through text-slate-500' : 'text-slate-200'}`}>
                    {task.title}
                  </Text>
                  {task.due_date && !task.completed && (
                    <Text className="text-slate-500 text-xs mt-0.5">📅 {task.due_date}</Text>
                  )}
                </View>
              </View>

              <View
                className="px-2 py-0.5 rounded-md"
                style={{ backgroundColor: PRIORITY_COLORS[task.priority] + '25' }}
              >
                <Text className="text-xs font-bold" style={{ color: PRIORITY_COLORS[task.priority] }}>
                  {PRIORITY_LABELS[task.priority]}
                </Text>
              </View>
            </TouchableOpacity>
          ))
        )}
        <Text className="text-slate-600 text-xs text-center mt-2 mb-6">
          Mantén presionado para eliminar una tarea
        </Text>
        <View className="h-6" />
      </ScrollView>
    </SafeAreaView>
  );
}