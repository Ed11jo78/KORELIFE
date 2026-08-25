import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, Modal, KeyboardAvoidingView, Platform, StatusBar } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Plus, Send, X, User, CheckCheck, Bot, Headphones, MessageCircle, ChevronLeft } from 'lucide-react-native';
import { useSocialStore, DirectChat } from '../../../src/store/socialStore';

export default function MessagesScreen() {
  const router = useRouter();
  const { directChats, directMessages, sendDirectMessage, startDirectChat } = useSocialStore();

  const [activeChat, setActiveChat] = useState<DirectChat | null>(null);
  const [inputText, setInputText] = useState('');
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const [newUserName, setNewUserName] = useState('');
  const [newInitialMsg, setNewInitialMsg] = useState('');

  const handleSend = () => {
    if (!inputText.trim() || !activeChat) return;
    sendDirectMessage(activeChat.id, inputText.trim());
    setInputText('');
  };

  const handleCreateChat = () => {
    if (!newUserName.trim() || !newInitialMsg.trim()) return;
    const chatId = startDirectChat(newUserName.trim(), newInitialMsg.trim());
    setShowNewChatModal(false);
    setNewUserName('');
    setNewInitialMsg('');
    const created = directChats.find(c => c.id === chatId);
    if (created) setActiveChat(created);
  };

  const currentMessages = activeChat ? directMessages[activeChat.id] || [] : [];

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-4">
      {/* Cabecera */}
      <View className="flex-row items-center justify-between py-4 mb-3">
        <TouchableOpacity 
          onPress={() => router.back()} 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="p-2.5 rounded-2xl border"
          hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
        >
          <ArrowLeft size={20} color="#ffffff" />
        </TouchableOpacity>
        <Text style={{ color: '#ffffff' }} className="text-lg font-black">Mensajes Directos</Text>
        <TouchableOpacity 
          onPress={() => setShowNewChatModal(true)} 
          style={{ backgroundColor: '#10b981' }}
          className="p-2.5 rounded-full shadow-lg active:bg-emerald-600"
          hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
        >
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      {/* Lista de Conversaciones */}
      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        <Text style={{ color: '#94a3b8' }} className="text-xs font-bold uppercase tracking-wider mb-3">
          Conversaciones Activas
        </Text>

        {directChats.map((c) => (
          <TouchableOpacity
            key={c.id}
            onPress={() => setActiveChat(c)}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
            className="p-4 rounded-3xl mb-3 flex-row items-center justify-between shadow-sm active:opacity-80"
          >
            <View className="flex-row items-center flex-1 mr-3">
              <View 
                style={{ 
                  backgroundColor: c.id === '1' ? '#1e1b4b' : c.id === '2' ? '#064e3b' : '#334155',
                  borderColor: c.id === '1' ? '#6366f1' : c.id === '2' ? '#10b981' : '#475569',
                  borderWidth: 1.5,
                  width: 48,
                  height: 48,
                  borderRadius: 18
                }}
                className="items-center justify-center mr-3.5 relative"
              >
                {c.id === '1' ? (
                  <Bot size={22} color="#818cf8" />
                ) : c.id === '2' ? (
                  <Headphones size={22} color="#34d399" />
                ) : (
                  <Text style={{ color: '#ffffff' }} className="text-base font-black">{c.avatarText}</Text>
                )}
                {c.online && (
                  <View 
                    style={{ backgroundColor: '#10b981', borderColor: '#0f172a' }} 
                    className="absolute -top-0.5 -right-0.5 w-3.5 h-3.5 rounded-full border-2" 
                  />
                )}
              </View>

              <View className="flex-1">
                <View className="flex-row justify-between items-center mb-0.5">
                  <Text style={{ color: '#ffffff' }} className="font-bold text-sm" numberOfLines={1}>
                    {c.name}
                  </Text>
                  <Text style={{ color: '#64748b' }} className="text-[11px] font-medium">{c.time}</Text>
                </View>
                <Text style={{ color: '#94a3b8' }} className="text-xs" numberOfLines={1}>
                  {c.lastMessage}
                </Text>
              </View>
            </View>

            {c.unread > 0 && (
              <View style={{ backgroundColor: '#10b981' }} className="w-5 h-5 rounded-full items-center justify-center shadow-sm">
                <Text style={{ color: '#022c22' }} className="text-[10px] font-black">{c.unread}</Text>
              </View>
            )}
          </TouchableOpacity>
        ))}
        <View className="h-10" />
      </ScrollView>

      {/* MODAL DE CHAT INTERACTIVO (CON SAFE INSETS PARA NOTCH/DYNAMIC ISLAND) */}
      <Modal 
        visible={activeChat !== null} 
        animationType="slide" 
        transparent={false}
        onRequestClose={() => setActiveChat(null)}
      >
        <View style={{ flex: 1, backgroundColor: '#020617' }}>
          {/* Header del Chat con Padding Seguro para iOS Notch / Status Bar */}
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
                onPress={() => setActiveChat(null)} 
                style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
                className="p-2.5 rounded-2xl mr-3 flex-row items-center active:bg-slate-700"
                hitSlop={{ top: 15, bottom: 15, left: 15, right: 15 }}
              >
                <ChevronLeft size={22} color="#34d399" />
              </TouchableOpacity>
              <View className="flex-1">
                <Text style={{ color: '#ffffff' }} className="font-black text-base" numberOfLines={1}>
                  {activeChat?.name}
                </Text>
                <Text style={{ color: activeChat?.online ? '#34d399' : '#94a3b8' }} className="text-xs font-bold mt-0.5">
                  {activeChat?.online ? '● En línea' : 'Desconectado'}
                </Text>
              </View>
            </View>

            <TouchableOpacity 
              onPress={() => setActiveChat(null)}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
              className="p-2.5 rounded-2xl"
              hitSlop={{ top: 15, bottom: 15, left: 15, right: 15 }}
            >
              <X size={18} color="#94a3b8" />
            </TouchableOpacity>
          </View>

          {/* Historial de Mensajes */}
          <ScrollView className="flex-1 p-4" showsVerticalScrollIndicator={false}>
            {currentMessages.map((m) => (
              <View 
                key={m.id} 
                className={`mb-3 max-w-[82%] ${m.isMe ? 'self-end' : 'self-start'}`}
              >
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
                  <View className="flex-row items-center justify-end mt-1">
                    <Text style={{ color: m.isMe ? '#a7f3d0' : '#64748b' }} className="text-[10px] mr-1">
                      {m.time}
                    </Text>
                    {m.isMe && <CheckCheck size={12} color="#a7f3d0" />}
                  </View>
                </View>
              </View>
            ))}
          </ScrollView>

          {/* Campo de Entrada de Mensaje */}
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
                placeholder="Escribe un mensaje..."
                placeholderTextColor="#64748b"
                style={{ backgroundColor: '#1e293b', color: '#ffffff', borderColor: '#334155', borderWidth: 1 }}
                className="flex-1 px-4 py-3 rounded-2xl text-sm mr-2"
                onSubmitEditing={handleSend}
              />
              <TouchableOpacity 
                onPress={handleSend}
                style={{ backgroundColor: '#10b981' }}
                className="p-3.5 rounded-2xl shadow-lg active:bg-emerald-600"
              >
                <Send size={18} color="#022c22" strokeWidth={2.5} />
              </TouchableOpacity>
            </View>
          </KeyboardAvoidingView>
        </View>
      </Modal>

      {/* MODAL: INICIAR NUEVO CHAT DIRECTO */}
      <Modal 
        visible={showNewChatModal} 
        animationType="slide" 
        transparent={true}
        onRequestClose={() => setShowNewChatModal(false)}
      >
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text style={{ color: '#ffffff' }} className="text-xl font-black">Nuevo Mensaje ✉️</Text>
              <TouchableOpacity onPress={() => setShowNewChatModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-2 uppercase">Nombre del Contacto / Usuario</Text>
            <TextInput
              value={newUserName}
              onChangeText={setNewUserName}
              placeholder="Ej: Daniel Rivas, María López..."
              placeholderTextColor="#64748b"
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff', borderWidth: 1 }}
              className="p-3.5 rounded-2xl text-sm mb-4 font-medium"
            />

            <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-2 uppercase">Primer Mensaje</Text>
            <TextInput
              value={newInitialMsg}
              onChangeText={setNewInitialMsg}
              placeholder="Escribe tu mensaje inicial..."
              placeholderTextColor="#64748b"
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff', borderWidth: 1 }}
              className="p-3.5 rounded-2xl text-sm mb-6 font-medium"
            />

            <TouchableOpacity 
              onPress={handleCreateChat}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600"
            >
              <Text style={{ color: '#022c22' }} className="font-black text-base">Iniciar Conversación ✅</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}