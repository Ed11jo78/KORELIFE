import React, { useState, useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, Modal, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Plus, Search, FileText, Trash2, X } from 'lucide-react-native';
import { useTasksStore } from '../../../src/store/tasksStore';

export default function NotesScreen() {
  const router = useRouter();
  const notes = useTasksStore((s) => s.notes);
  const addNote = useTasksStore((s) => s.addNote);
  const deleteNote = useTasksStore((s) => s.deleteNote);
  const [search, setSearch] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [newNote, setNewNote] = useState({ title: '', content: '' });

  const filtered = notes.filter(
    (n) =>
      n.title.toLowerCase().includes(search.toLowerCase()) ||
      n.content.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreate = useCallback(() => {
    if (!newNote.title.trim()) {
      Alert.alert('Título requerido', 'Ingresa un título para la nota.');
      return;
    }
    addNote({ user_id: '1', title: newNote.title.trim(), content: newNote.content.trim() });
    setNewNote({ title: '', content: '' });
    setShowModal(false);
  }, [newNote, addNote]);

  const handleDelete = useCallback(
    (id: string, title: string) => {
      Alert.alert('Eliminar nota', `¿Eliminar "${title}"?`, [
        { text: 'Cancelar', style: 'cancel' },
        { text: 'Eliminar', style: 'destructive', onPress: () => deleteNote(id) },
      ]);
    },
    [deleteNote]
  );

  const formatDate = (isoStr: string) => {
    return new Date(isoStr).toLocaleDateString('es-VE', { day: 'numeric', month: 'short' });
  };

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-4">
      {/* Header */}
      <View className="flex-row items-center justify-between py-4 mb-3">
        <TouchableOpacity onPress={() => router.back()} className="p-2 bg-slate-900 rounded-full border border-slate-800">
          <ArrowLeft size={20} color="white" />
        </TouchableOpacity>
        <Text className="text-white text-lg font-bold">Notas e Ideas</Text>
        <TouchableOpacity onPress={() => setShowModal(true)} className="p-2 bg-emerald-500 rounded-full">
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      {/* Search */}
      <View className="flex-row items-center bg-slate-900 rounded-2xl px-3 py-2 border border-slate-800 mb-4">
        <Search size={16} color="#64748b" />
        <TextInput
          value={search}
          onChangeText={setSearch}
          placeholder="Buscar notas..."
          placeholderTextColor="#64748b"
          className="flex-1 ml-2 text-white text-sm"
        />
        {search.length > 0 && (
          <TouchableOpacity onPress={() => setSearch('')}>
            <X size={16} color="#64748b" />
          </TouchableOpacity>
        )}
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Stats */}
        <View className="flex-row gap-2 mb-4">
          <View className="flex-1 bg-slate-900 border border-slate-800 rounded-xl p-3">
            <Text className="text-slate-400 text-xs">Total de notas</Text>
            <Text className="text-white font-black text-xl">{notes.length}</Text>
          </View>
          <View className="flex-1 bg-indigo-500/10 border border-indigo-500/20 rounded-xl p-3">
            <Text className="text-indigo-400 text-xs">Resultados</Text>
            <Text className="text-indigo-400 font-black text-xl">{filtered.length}</Text>
          </View>
        </View>

        {filtered.length === 0 && (
          <View className="items-center py-12">
            <FileText size={40} color="#334155" />
            <Text className="text-slate-500 text-sm font-medium mt-3">
              {search ? 'Sin resultados para esa búsqueda' : 'No tienes notas aún'}
            </Text>
          </View>
        )}

        {filtered.map((n) => (
          <View key={n.id} className="bg-slate-900 p-4 rounded-2xl mb-3 border border-slate-800">
            <View className="flex-row justify-between items-start mb-2">
              <View className="flex-1 mr-3">
                <Text className="text-white font-bold text-sm">{n.title}</Text>
                <Text className="text-slate-500 text-xs mt-0.5">{formatDate(n.created_at)}</Text>
              </View>
              <TouchableOpacity
                onPress={() => handleDelete(n.id, n.title)}
                className="p-1.5 bg-red-500/15 rounded-lg"
              >
                <Trash2 size={14} color="#f87171" />
              </TouchableOpacity>
            </View>
            <Text className="text-slate-400 text-xs leading-relaxed" numberOfLines={4}>
              {n.content || 'Sin contenido...'}
            </Text>
          </View>
        ))}
        <View className="h-6" />
      </ScrollView>

      {/* Create Note Modal */}
      <Modal visible={showModal} transparent animationType="slide" onRequestClose={() => setShowModal(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center mb-5">
              <Text className="text-white text-xl font-black">Nueva Nota 📝</Text>
              <TouchableOpacity onPress={() => setShowModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="white" />
              </TouchableOpacity>
            </View>

            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Título</Text>
            <TextInput
              value={newNote.title}
              onChangeText={(v) => setNewNote((n) => ({ ...n, title: v }))}
              placeholder="ej. Ideas para ahorrar..."
              placeholderTextColor="#475569"
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-4 border border-slate-700"
            />

            <Text className="text-slate-400 text-xs font-semibold uppercase mb-2">Contenido</Text>
            <TextInput
              value={newNote.content}
              onChangeText={(v) => setNewNote((n) => ({ ...n, content: v }))}
              placeholder="Escribe tu nota aquí..."
              placeholderTextColor="#475569"
              multiline
              numberOfLines={4}
              textAlignVertical="top"
              className="bg-slate-800 text-white rounded-xl px-4 py-3 mb-5 border border-slate-700"
              style={{ minHeight: 100 }}
            />

            <TouchableOpacity onPress={handleCreate} className="bg-emerald-500 py-4 rounded-2xl items-center">
              <Text className="text-slate-950 font-black text-base">Guardar Nota 📝</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}