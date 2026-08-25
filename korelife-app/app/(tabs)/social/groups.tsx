import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, Modal, KeyboardAvoidingView, Platform, StatusBar } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Users, Plus, ChevronRight, TrendingUp, Sparkles, BookOpen, Send, X, Check, MessageSquare, ChevronLeft } from 'lucide-react-native';
import { useSocialStore, Group } from '../../../src/store/socialStore';

export default function GroupsScreen() {
  const router = useRouter();
  const { groups, groupMessages, toggleJoinGroup, createGroup, sendGroupMessage } = useSocialStore();

  const [activeGroup, setActiveGroup] = useState<Group | null>(null);
  const [inputText, setInputText] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newGroupName, setNewGroupName] = useState('');
  const [newGroupDesc, setNewGroupDesc] = useState('');
  const [newGroupCat, setNewGroupCat] = useState('Finanzas');

  const getIcon = (name: string) => {
    switch (name) {
      case 'TrendingUp': return <TrendingUp size={22} color="#34d399" />;
      case 'Sparkles': return <Sparkles size={22} color="#fbbf24" />;
      case 'BookOpen': return <BookOpen size={22} color="#818cf8" />;
      default: return <Users size={22} color="#38bdf8" />;
    }
  };

  const handleSendMessage = () => {
    if (!inputText.trim() || !activeGroup) return;
    sendGroupMessage(activeGroup.id, inputText.trim());
    setInputText('');
  };

  const handleCreateGroup = () => {
    if (!newGroupName.trim() || !newGroupDesc.trim()) return;
    createGroup(newGroupName.trim(), newGroupDesc.trim(), newGroupCat);
    setShowCreateModal(false);
    setNewGroupName('');
    setNewGroupDesc('');
  };

  const currentMessages = activeGroup ? groupMessages[activeGroup.id] || [] : [];

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-4">
      {/* Header */}
      <View className="flex-row items-center justify-between py-4 mb-3">
        <TouchableOpacity 
          onPress={() => router.back()} 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="p-2.5 rounded-2xl border"
          hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
        >
          <ArrowLeft size={20} color="#ffffff" />
        </TouchableOpacity>
        <Text style={{ color: '#ffffff' }} className="text-lg font-black">Comunidades & Grupos</Text>
        <TouchableOpacity 
          onPress={() => setShowCreateModal(true)} 
          style={{ backgroundColor: '#10b981' }}
          className="p-2.5 rounded-full shadow-lg active:bg-emerald-600"
          hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
        >
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        <Text style={{ color: '#94a3b8' }} className="text-xs font-bold uppercase tracking-wider mb-3">
          Tus Comunidades Activas ({groups.length})
        </Text>

        {groups.map((g) => (
          <TouchableOpacity
            key={g.id}
            onPress={() => setActiveGroup(g)}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
            className="p-4 rounded-3xl mb-3 shadow-md active:opacity-80"
          >
            <View className="flex-row items-center justify-between mb-2">
              <View className="flex-row items-center flex-1 mr-2">
                <View style={{ backgroundColor: '#1e293b', borderColor: '#334155' }} className="w-12 h-12 rounded-2xl items-center justify-center mr-3 border">
                  {getIcon(g.iconName)}
                </View>
                <View className="flex-1">
                  <Text style={{ color: '#ffffff' }} className="font-bold text-sm" numberOfLines={1}>
                    {g.name}
                  </Text>
                  <Text style={{ color: '#34d399' }} className="text-[11px] font-bold mt-0.5">
                    👥 {g.members.toLocaleString()} miembros
                  </Text>
                </View>
              </View>

              <TouchableOpacity
                onPress={(e) => {
                  e.stopPropagation();
                  toggleJoinGroup(g.id);
                }}
                style={{
                  backgroundColor: g.joined ? '#064e3b' : '#1e293b',
                  borderColor: g.joined ? '#10b981' : '#475569',
                  borderWidth: 1
                }}
                className="px-3 py-1.5 rounded-xl flex-row items-center"
              >
                {g.joined && <Check size={12} color="#34d399" style={{ marginRight: 4 }} />}
                <Text style={{ color: g.joined ? '#34d399' : '#94a3b8' }} className="text-xs font-bold">
                  {g.joined ? 'Unido' : '+ Unirme'}
                </Text>
              </TouchableOpacity>
            </View>

            <Text style={{ color: '#94a3b8' }} className="text-xs leading-relaxed mt-1" numberOfLines={2}>
              {g.desc}
            </Text>

            <View style={{ borderTopColor: '#1e293b' }} className="flex-row justify-between items-center mt-3 pt-2.5 border-t">
              <Text style={{ color: '#64748b' }} className="text-[11px] font-medium">Categoría: {g.category}</Text>
              <View className="flex-row items-center">
                <Text style={{ color: '#38bdf8' }} className="text-xs font-bold mr-1">Ver Debate</Text>
                <ChevronRight size={14} color="#38bdf8" />
              </View>
            </View>
          </TouchableOpacity>
        ))}
        <View className="h-10" />
      </ScrollView>

      {/* MODAL DE DEBATE Y CHAT GRUPAL (CON SAFE INSETS PARA NOTCH/DYNAMIC ISLAND) */}
      <Modal 
        visible={activeGroup !== null} 
        animationType="slide" 
        transparent={false}
        onRequestClose={() => setActiveGroup(null)}
      >
        <View style={{ flex: 1, backgroundColor: '#020617' }}>
          {/* Header del Grupo */}
          <View 
            style={{ 
              backgroundColor: '#0f172a', 
              borderBottomColor: '#1e293b', 
              borderBottomWidth: 1,
              paddingTop: Platform.OS === 'ios' ? 54 : (StatusBar.currentHeight || 12) + 10,
              paddingBottom: 14,
              paddingHorizontal: 16
            }} 
            className="flex-row items-center justify-between shadow-lg"
          >
            <View className="flex-row items-center flex-1 mr-2">
              <TouchableOpacity 
                onPress={() => setActiveGroup(null)} 
                style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
                className="p-2.5 rounded-2xl mr-3 flex-row items-center active:bg-slate-700"
                hitSlop={{ top: 15, bottom: 15, left: 15, right: 15 }}
              >
                <ChevronLeft size={22} color="#34d399" />
              </TouchableOpacity>
              <View className="flex-1">
                <Text style={{ color: '#ffffff' }} className="font-black text-base" numberOfLines={1}>
                  {activeGroup?.name}
                </Text>
                <Text style={{ color: '#34d399' }} className="text-xs font-bold mt-0.5">
                  {activeGroup?.members.toLocaleString()} participantes
                </Text>
              </View>
            </View>
            <TouchableOpacity 
              onPress={() => setActiveGroup(null)}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
              className="p-2.5 rounded-2xl"
              hitSlop={{ top: 15, bottom: 15, left: 15, right: 15 }}
            >
              <X size={18} color="#94a3b8" />
            </TouchableOpacity>
          </View>

          {/* Mensajes del Grupo */}
          <ScrollView className="flex-1 p-4" showsVerticalScrollIndicator={false}>
            {currentMessages.length === 0 ? (
              <View className="items-center py-16">
                <MessageSquare size={36} color="#64748b" />
                <Text style={{ color: '#94a3b8' }} className="text-sm font-bold mt-2">
                  ¡Sé el primero en iniciar el debate en este grupo!
                </Text>
              </View>
            ) : (
              currentMessages.map((m) => (
                <View 
                  key={m.id} 
                  className={`mb-3 max-w-[85%] ${m.isMe ? 'self-end' : 'self-start'}`}
                >
                  {!m.isMe && (
                    <Text style={{ color: '#60a5fa' }} className="text-[11px] font-bold mb-1 ml-1">
                      {m.author}
                    </Text>
                  )}
                  <View 
                    style={{ 
                      backgroundColor: m.isMe ? '#047857' : '#0f172a',
                      borderColor: m.isMe ? '#10b981' : '#334155',
                      borderWidth: 1
                    }}
                    className="p-3.5 rounded-2xl shadow-sm"
                  >
                    <Text style={{ color: '#ffffff' }} className="text-sm font-medium leading-relaxed">
                      {m.text}
                    </Text>
                    <Text style={{ color: m.isMe ? '#a7f3d0' : '#64748b' }} className="text-[10px] text-right mt-1 font-medium">
                      {m.time}
                    </Text>
                  </View>
                </View>
              ))
            )}
          </ScrollView>

          {/* Campo para Escribir Mensaje Grupal */}
          <KeyboardAvoidingView 
            behavior={Platform.OS === 'ios' ? 'padding' : undefined}
            keyboardVerticalOffset={Platform.OS === 'ios' ? 10 : 0}
          >
            <View 
              style={{ 
                backgroundColor: '#0f172a', 
                borderTopColor: '#1e293b', 
                borderTopWidth: 1,
                paddingBottom: Platform.OS === 'ios' ? 24 : 12
              }} 
              className="p-3 flex-row items-center"
            >
              <TextInput
                value={inputText}
                onChangeText={setInputText}
                placeholder="Escribe al grupo de la comunidad..."
                placeholderTextColor="#64748b"
                style={{ backgroundColor: '#1e293b', color: '#ffffff', borderColor: '#334155', borderWidth: 1 }}
                className="flex-1 px-4 py-3 rounded-2xl text-sm mr-2"
                onSubmitEditing={handleSendMessage}
              />
              <TouchableOpacity 
                onPress={handleSendMessage}
                style={{ backgroundColor: '#10b981' }}
                className="p-3.5 rounded-2xl shadow-lg active:bg-emerald-600"
              >
                <Send size={18} color="#022c22" strokeWidth={2.5} />
              </TouchableOpacity>
            </View>
          </KeyboardAvoidingView>
        </View>
      </Modal>

      {/* MODAL: CREAR GRUPO DE COMUNIDAD */}
      <Modal 
        visible={showCreateModal} 
        animationType="slide" 
        transparent={true}
        onRequestClose={() => setShowCreateModal(false)}
      >
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text style={{ color: '#ffffff' }} className="text-xl font-black">Crear Nuevo Grupo 👥</Text>
              <TouchableOpacity onPress={() => setShowCreateModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-2 uppercase">Nombre de la Comunidad</Text>
            <TextInput
              value={newGroupName}
              onChangeText={setNewGroupName}
              placeholder="Ej: Ahorristas en Dólares..."
              placeholderTextColor="#64748b"
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff', borderWidth: 1 }}
              className="p-3.5 rounded-2xl text-sm mb-4 font-medium"
            />

            <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-2 uppercase">Descripción / Propósito</Text>
            <TextInput
              value={newGroupDesc}
              onChangeText={setNewGroupDesc}
              placeholder="¿De qué trata este grupo?..."
              placeholderTextColor="#64748b"
              multiline
              numberOfLines={3}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff', borderWidth: 1 }}
              className="p-3.5 rounded-2xl text-sm mb-6 font-medium"
            />

            <TouchableOpacity 
              onPress={handleCreateGroup}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600"
            >
              <Text style={{ color: '#022c22' }} className="font-black text-base">Crear Comunidad 🚀</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}