import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Modal, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import { X, Calendar, Flag, Check, Plus } from 'lucide-react-native';
import { useTasksStore } from '../../src/store/tasksStore';

interface Props {
  visible: boolean;
  onClose: () => void;
}

export default function AddTaskSheet({ visible, onClose }: Props) {
  const addTask = useTasksStore((s) => s.addTask);
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState<'low' | 'medium' | 'high'>('medium');
  const [dueDate, setDueDate] = useState('Hoy');

  const handleSave = () => {
    if (!title.trim()) {
      Alert.alert('Título requerido', 'Por favor ingresa la descripción de la tarea.');
      return;
    }

    addTask({
      user_id: '1',
      title: title.trim(),
      completed: false,
      priority,
      due_date: new Date().toISOString().split('T')[0]
    });

    setTitle('');
    onClose();
  };

  return (
    <Modal 
      visible={visible} 
      animationType="slide" 
      transparent={true} 
      onRequestClose={onClose}
      statusBarTranslucent={true}
    >
      <KeyboardAvoidingView 
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
        style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}
      >
        <View 
          style={{ 
            backgroundColor: '#0f172a', 
            borderTopLeftRadius: 28, 
            borderTopRightRadius: 28, 
            borderTopWidth: 1, 
            borderColor: '#334155',
            paddingBottom: Platform.OS === 'ios' ? 30 : 20
          }}
          className="p-6"
        >
          {/* Visual handle */}
          <View className="items-center mb-3">
            <View style={{ width: 42, height: 5, backgroundColor: '#475569', borderRadius: 10 }} />
          </View>

          {/* Header */}
          <View className="flex-row justify-between items-center mb-5 pb-3 border-b border-slate-800">
            <Text className="text-xl font-black text-white">Nueva Tarea ✨</Text>
            <TouchableOpacity 
              onPress={onClose}
              style={{ backgroundColor: '#1e293b' }}
              className="p-2 rounded-full border border-slate-700"
            >
              <X size={18} color="#94a3b8" />
            </TouchableOpacity>
          </View>

          {/* Title Input */}
          <TextInput 
            value={title}
            onChangeText={setTitle}
            placeholder="¿Qué necesitas hacer?..." 
            placeholderTextColor="#64748b"
            style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
            className="text-white text-base p-4 rounded-2xl border mb-4 font-medium"
            autoFocus
          />

          {/* Priority selector */}
          <Text className="text-slate-300 text-xs font-bold uppercase mb-2">Prioridad</Text>
          <View className="flex-row gap-2 mb-6">
            {(['low', 'medium', 'high'] as const).map((p) => {
              const isSel = priority === p;
              const label = p === 'high' ? 'Alta' : p === 'medium' ? 'Media' : 'Baja';
              const color = p === 'high' ? '#ef4444' : p === 'medium' ? '#f59e0b' : '#10b981';
              return (
                <TouchableOpacity
                  key={p}
                  onPress={() => setPriority(p)}
                  style={{
                    backgroundColor: isSel ? '#1e293b' : '#0f172a',
                    borderColor: isSel ? color : '#334155',
                    borderWidth: 1.5
                  }}
                  className="flex-1 py-2.5 rounded-xl items-center flex-row justify-center"
                >
                  <View style={{ width: 8, height: 8, borderRadius: 4, backgroundColor: color, marginRight: 6 }} />
                  <Text className={`text-xs font-bold ${isSel ? 'text-white' : 'text-slate-400'}`}>
                    {label}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Save Button */}
          <TouchableOpacity 
            onPress={handleSave}
            style={{ backgroundColor: '#10b981' }}
            className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600 mb-2"
          >
            <Text className="text-slate-950 font-black text-base">Guardar Tarea ✅</Text>
          </TouchableOpacity>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}
