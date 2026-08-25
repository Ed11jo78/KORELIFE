import React, { useState, useCallback, useMemo } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Modal, TextInput, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Plus, FolderOpen, CheckCircle2, Circle, ChevronRight, X, Pencil, Trash2 } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';

const PROJECT_COLORS = ['#10b981', '#6366f1', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#f97316', '#ec4899'];
const PROJECT_EMOJIS = ['📁', '💼', '🏠', '💰', '📚', '🎯', '🚀', '🎨', '🔧', '🌿'];

export default function ProjectsScreen() {
  const projects = useTasksStore((s) => s.projects);
  const addProject = useTasksStore((s) => s.addProject);
  const [showModal, setShowModal] = useState(false);
  const [newProject, setNewProject] = useState({ name: '', description: '', emoji: '📁', color: '#10b981' });

  const handleCreate = useCallback(() => {
    if (!newProject.name.trim()) {
      Alert.alert('Nombre requerido', 'Por favor ingresa el nombre del proyecto.');
      return;
    }
    addProject({
      name: newProject.name.trim(),
      description: newProject.description.trim(),
      color: newProject.color,
      tasks: 0,
      completedTasks: 0,
    });
    setNewProject({ name: '', description: '', emoji: '📁', color: '#10b981' });
    setShowModal(false);
  }, [newProject, addProject]);

  const totalTasks = useMemo(() => projects.reduce((s, p) => s + p.tasks, 0), [projects]);
  const completedTasks = useMemo(() => projects.reduce((s, p) => s + p.completedTasks, 0), [projects]);

  return (
    <SafeAreaView className="flex-1 bg-slate-950">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View className="px-5 pt-4 pb-6">
          <View className="flex-row justify-between items-center">
            <View>
              <Text className="text-slate-400 text-xs font-semibold uppercase tracking-widest">Tareas</Text>
              <Text className="text-white text-2xl font-black">Proyectos 🚀</Text>
            </View>
            <TouchableOpacity
              onPress={() => setShowModal(true)}
              className="bg-indigo-500 w-10 h-10 rounded-full items-center justify-center"
            >
              <Plus size={22} color="white" strokeWidth={2.5} />
            </TouchableOpacity>
          </View>

          {/* Stats */}
          <View className="flex-row gap-3 mt-5">
            <View className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl p-3.5">
              <Text className="text-slate-400 text-xs">Proyectos</Text>
              <Text className="text-white text-2xl font-black">{projects.length}</Text>
            </View>
            <View className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl p-3.5">
              <Text className="text-slate-400 text-xs">Tareas totales</Text>
              <Text className="text-white text-2xl font-black">{totalTasks}</Text>
            </View>
            <View className="flex-1 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl p-3.5">
              <Text className="text-emerald-400 text-xs">Completadas</Text>
              <Text className="text-emerald-400 text-2xl font-black">{completedTasks}</Text>
            </View>
          </View>
        </View>

        {/* Projects Grid */}
        <View className="px-5">
          <View className="flex-row flex-wrap" style={{ gap: 12 }}>
            {projects.map((proj) => {
              const pct = proj.tasks > 0 ? (proj.completedTasks / proj.tasks) * 100 : 0;
              const remaining = proj.tasks - proj.completedTasks;
              return (
                <TouchableOpacity
                  key={proj.id}
                  className="bg-slate-900 border border-slate-800 rounded-2xl p-4"
                  style={{ width: '47%' }}
                >
                  {/* Top */}
                  <View className="w-12 h-12 rounded-xl items-center justify-center mb-3" style={{ backgroundColor: proj.color + '25' }}>
                    <View className="w-6 h-6 rounded-md" style={{ backgroundColor: proj.color }} />
                  </View>

                  <Text className="text-white font-bold text-sm mb-0.5" numberOfLines={1}>{proj.name}</Text>
                  {proj.description && (
                    <Text className="text-slate-500 text-xs mb-2" numberOfLines={2}>{proj.description}</Text>
                  )}

                  {/* Progress */}
                  <View className="h-1.5 bg-slate-800 rounded-full overflow-hidden mb-1.5">
                    <View
                      className="h-full rounded-full"
                      style={{ width: `${pct}%`, backgroundColor: proj.color }}
                    />
                  </View>

                  <View className="flex-row justify-between">
                    <Text className="text-slate-500 text-xs">{proj.tasks} tareas</Text>
                    <Text className="text-xs font-bold" style={{ color: proj.color }}>{pct.toFixed(0)}%</Text>
                  </View>

                  {proj.deadline && (
                    <Text className="text-slate-600 text-xs mt-1">📅 {proj.deadline}</Text>
                  )}
                </TouchableOpacity>
              );
            })}

            {/* Add Project Button */}
            <TouchableOpacity
              onPress={() => setShowModal(true)}
              className="border-2 border-dashed border-slate-700 rounded-2xl items-center justify-center py-8"
              style={{ width: '47%' }}
            >
              <Plus size={24} color="#64748b" />
              <Text className="text-slate-500 text-xs mt-2 text-center">Nuevo{'\n'}Proyecto</Text>
            </TouchableOpacity>
          </View>

          {/* Full width projects */}
          <View className="mt-6 mb-8">
            <Text className="text-slate-400 text-xs font-bold uppercase mb-3">Vista detallada</Text>
            {projects.map((proj) => {
              const pct = proj.tasks > 0 ? (proj.completedTasks / proj.tasks) * 100 : 0;
              return (
                <View key={proj.id + '_detail'} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 mb-3">
                  <View className="flex-row items-center justify-between mb-3">
                    <View className="flex-row items-center flex-1">
                      <View className="w-3 h-10 rounded-full mr-3" style={{ backgroundColor: proj.color }} />
                      <View className="flex-1">
                        <Text className="text-white font-bold">{proj.name}</Text>
                        <Text className="text-slate-500 text-xs">{proj.completedTasks} de {proj.tasks} tareas completadas</Text>
                      </View>
                    </View>
                    <View className="items-center">
                      <Text className="font-black text-lg" style={{ color: proj.color }}>{pct.toFixed(0)}%</Text>
                    </View>
                  </View>
                  <View className="h-2 bg-slate-800 rounded-full overflow-hidden">
                    <View className="h-full rounded-full" style={{ width: `${pct}%`, backgroundColor: proj.color }} />
                  </View>
                </View>
              );
            })}
          </View>
        </View>
      </ScrollView>

      {/* Create Project Modal */}
      <Modal visible={showModal} transparent animationType="slide" onRequestClose={() => setShowModal(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center mb-5">
              <Text className="text-white text-xl font-black">Nuevo Proyecto 🚀</Text>
              <TouchableOpacity onPress={() => setShowModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>

            {/* Color Picker */}
            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Color</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} className="mb-4">
              {PROJECT_COLORS.map((color) => (
                <TouchableOpacity
                  key={color}
                  onPress={() => setNewProject((p) => ({ ...p, color }))}
                  className={`w-8 h-8 rounded-full mr-2 items-center justify-center ${newProject.color === color ? 'border-2 border-white' : ''}`}
                  style={{ backgroundColor: color }}
                >
                  {newProject.color === color && <View className="w-3 h-3 rounded-full bg-white" />}
                </TouchableOpacity>
              ))}
            </ScrollView>

            {/* Name */}
            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Nombre del Proyecto</Text>
            <TextInput
              value={newProject.name}
              onChangeText={(v) => setNewProject((p) => ({ ...p, name: v }))}
              placeholder="ej. Finanzas 2026"
              placeholderTextColor="#475569"
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-4 border border-slate-700"
            />

            {/* Description */}
            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Descripción (opcional)</Text>
            <TextInput
              value={newProject.description}
              onChangeText={(v) => setNewProject((p) => ({ ...p, description: v }))}
              placeholder="Descripción breve del proyecto..."
              placeholderTextColor="#475569"
              multiline
              numberOfLines={2}
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-5 border border-slate-700"
            />

            <TouchableOpacity onPress={handleCreate} className="bg-indigo-500 py-4 rounded-2xl items-center">
              <Text className="text-white font-black text-base">Crear Proyecto</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}
