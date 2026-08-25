import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput, Modal, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Search, ArrowLeft, MessageSquare, AlertTriangle, HelpCircle, ChevronRight, CheckCircle2, Shield, CreditCard, User, X } from 'lucide-react-native';
import { useSocialStore } from '../../../src/store/socialStore';

export default function SupportScreen() {
  const router = useRouter();
  const { tickets, createTicket } = useSocialStore();
  const [search, setSearch] = useState('');
  const [showTicketModal, setShowTicketModal] = useState(false);
  const [ticketSubject, setTicketSubject] = useState('');
  const [ticketCategory, setTicketCategory] = useState('Tasas & Finanzas');
  const [ticketMsg, setTicketMsg] = useState('');

  const faqs = [
    { q: '¿Cómo se actualizan las tasas del BCV y Binance?', a: 'KoreLife consulta las cotizaciones oficiales de las APIs y del mercado P2P. Puedes refrescarlas tocando el botón "Actualizar" en el Dashboard.' },
    { q: '¿Puedo llevar cuentas en Bolívares y Dólares al mismo tiempo?', a: 'Sí. KoreLife es 100% multimoneda y calcula el patrimonio total consolidado con la tasa de cambio que tú elijas.' },
    { q: '¿Mis datos financieros están seguros?', a: 'Absolutamente. Todos los datos están cifrados de extremo a extremo y protegidos por autenticación biométrica y Supabase SSL.' },
    { q: '¿Cómo funcionan los grupos y mensajes directos?', a: 'Puedes unirte a comunidades temáticas para debatir y chatear directamente con otros usuarios o el asistente de IA.' }
  ];

  const filteredFaqs = faqs.filter(
    (f) => f.q.toLowerCase().includes(search.toLowerCase()) || f.a.toLowerCase().includes(search.toLowerCase())
  );

  const handleCreateTicket = () => {
    if (!ticketSubject.trim() || !ticketMsg.trim()) {
      Alert.alert('Campos requeridos', 'Por favor llena el asunto y el mensaje.');
      return;
    }
    createTicket(ticketSubject.trim(), ticketCategory, ticketMsg.trim());
    setShowTicketModal(false);
    setTicketSubject('');
    setTicketMsg('');
    Alert.alert('Ticket Creado', 'Tu solicitud ha sido recibida por el equipo de soporte.');
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-4">
      {/* Cabecera */}
      <View className="flex-row items-center justify-between py-4 mb-2">
        <TouchableOpacity 
          onPress={() => router.back()} 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="p-2.5 rounded-2xl border"
        >
          <ArrowLeft size={20} color="#ffffff" />
        </TouchableOpacity>
        <Text style={{ color: '#ffffff' }} className="text-lg font-black">Centro de Ayuda & FAQ</Text>
        <View className="w-9" />
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Buscador */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="flex-row items-center px-4 py-3.5 rounded-2xl border mb-5 shadow-sm"
        >
          <Search size={18} color="#94a3b8" />
          <TextInput
            value={search}
            onChangeText={setSearch}
            placeholder="¿En qué podemos ayudarte hoy?..."
            placeholderTextColor="#64748b"
            style={{ color: '#ffffff' }}
            className="flex-1 ml-3 text-sm font-medium"
          />
          {search.length > 0 && (
            <TouchableOpacity onPress={() => setSearch('')}>
              <X size={16} color="#94a3b8" />
            </TouchableOpacity>
          )}
        </View>

        {/* Categorías */}
        <Text style={{ color: '#ffffff' }} className="font-black text-base mb-3">Temas Frecuentes</Text>
        <View className="flex-row flex-wrap justify-between mb-5">
          {[
            { name: 'Mi Cuenta', icon: User, color: '#38bdf8' },
            { name: 'Tasas & Finanzas', icon: CreditCard, color: '#10b981' },
            { name: 'Productividad', icon: CheckCircle2, color: '#818cf8' },
            { name: 'Seguridad', icon: Shield, color: '#f59e0b' }
          ].map((cat, i) => (
            <TouchableOpacity 
              key={i} 
              onPress={() => setSearch(cat.name)}
              style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1, width: '48%' }}
              className="p-4 rounded-2xl mb-3 items-center shadow-sm active:opacity-80"
            >
              <View style={{ backgroundColor: '#1e293b' }} className="w-11 h-11 rounded-2xl items-center justify-center mb-2">
                <cat.icon size={22} color={cat.color} />
              </View>
              <Text style={{ color: '#ffffff' }} className="font-bold text-xs">{cat.name}</Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* Mis Tickets Activos */}
        {tickets.length > 0 && (
          <View className="mb-5">
            <Text style={{ color: '#ffffff' }} className="font-black text-base mb-3">Tus Tickets de Soporte</Text>
            {tickets.map((t) => (
              <View 
                key={t.id}
                style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
                className="p-4 rounded-2xl mb-2.5 shadow-sm"
              >
                <View className="flex-row justify-between items-center mb-1">
                  <Text style={{ color: '#38bdf8' }} className="font-bold text-xs">{t.id} • {t.category}</Text>
                  <View 
                    style={{ backgroundColor: t.status === 'resolved' ? '#064e3b' : '#1e1b4b' }}
                    className="px-2.5 py-0.5 rounded-lg"
                  >
                    <Text style={{ color: t.status === 'resolved' ? '#34d399' : '#818cf8' }} className="text-[10px] font-black uppercase">
                      {t.status === 'resolved' ? 'Resuelto' : 'En proceso'}
                    </Text>
                  </View>
                </View>
                <Text style={{ color: '#ffffff' }} className="font-bold text-sm">{t.subject}</Text>
              </View>
            ))}
          </View>
        )}

        {/* Preguntas Frecuentes */}
        <Text style={{ color: '#ffffff' }} className="font-black text-base mb-3">Preguntas Frecuentes</Text>
        {filteredFaqs.map((faq, i) => (
          <View 
            key={i} 
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
            className="p-4 rounded-2xl mb-3 shadow-sm"
          >
            <Text style={{ color: '#34d399' }} className="font-bold text-sm mb-1">{faq.q}</Text>
            <Text style={{ color: '#cbd5e1' }} className="text-xs leading-relaxed font-medium">{faq.a}</Text>
          </View>
        ))}

        {/* Contacto Directo */}
        <Text style={{ color: '#ffffff' }} className="font-black text-base mt-2 mb-3">Contacto Directo</Text>
        
        <TouchableOpacity 
          onPress={() => setShowTicketModal(true)}
          style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
          className="p-4 rounded-2xl flex-row items-center justify-between mb-3 shadow-sm active:opacity-80"
        >
          <View className="flex-row items-center flex-1 mr-2">
            <View style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981' }} className="w-11 h-11 rounded-2xl items-center justify-center mr-3 border">
              <MessageSquare size={20} color="#34d399" />
            </View>
            <View className="flex-1">
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm">Crear Ticket de Soporte</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs">Atención personalizada del equipo</Text>
            </View>
          </View>
          <ChevronRight size={18} color="#94a3b8" />
        </TouchableOpacity>

        <TouchableOpacity 
          onPress={() => {
            setTicketCategory('Reporte de Error');
            setTicketSubject('Reporte de Error');
            setShowTicketModal(true);
          }}
          style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
          className="p-4 rounded-2xl flex-row items-center justify-between mb-10 shadow-sm active:opacity-80"
        >
          <View className="flex-row items-center flex-1 mr-2">
            <View style={{ backgroundColor: 'rgba(244, 63, 94, 0.15)', borderColor: '#f43f5e' }} className="w-11 h-11 rounded-2xl items-center justify-center mr-3 border">
              <AlertTriangle size={20} color="#fb7185" />
            </View>
            <View className="flex-1">
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm">Reportar un Error / Bug</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs">Prioridad alta para el equipo técnico</Text>
            </View>
          </View>
          <ChevronRight size={18} color="#94a3b8" />
        </TouchableOpacity>
      </ScrollView>

      {/* MODAL: CREAR TICKET DE SOPORTE */}
      <Modal 
        visible={showTicketModal}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setShowTicketModal(false)}
      >
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text style={{ color: '#ffffff' }} className="text-xl font-black">Nuevo Ticket 🎫</Text>
              <TouchableOpacity onPress={() => setShowTicketModal(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold mb-2 uppercase">Asunto de la Consulta</Text>
            <TextInput
              value={ticketSubject}
              onChangeText={setTicketSubject}
              placeholder="Ej: Duda sobre conversión o configuración..."
              placeholderTextColor="#64748b"
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff' }}
              className="p-3.5 rounded-2xl border text-sm mb-4 font-medium"
            />

            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold mb-2 uppercase">Detalle del Mensaje</Text>
            <TextInput
              value={ticketMsg}
              onChangeText={setTicketMsg}
              placeholder="Explica detalladamente en qué podemos ayudarte..."
              placeholderTextColor="#64748b"
              multiline
              numberOfLines={4}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff' }}
              className="p-3.5 rounded-2xl border text-sm mb-6 font-medium"
            />

            <TouchableOpacity 
              onPress={handleCreateTicket}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600"
            >
              <Text style={{ color: '#022c22' }} className="font-black text-base">Enviar Solicitud ✅</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}