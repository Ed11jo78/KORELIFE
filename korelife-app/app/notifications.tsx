import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Feather } from '@expo/vector-icons';

export default function NotificationsScreen() {
  const router = useRouter();
  const [notifications, setNotifications] = useState([
    {
      id: '1',
      title: 'Salud Financiera Excelente 🟢',
      message: 'Has gastado un 15% menos de tu presupuesto semanal.',
      time: 'Hace 10 min',
      type: 'finance',
      read: false,
    },
    {
      id: '2',
      title: 'Próxima suscripción: Netflix',
      message: 'Se cargará $15.99 en 2 días a tu tarjeta principal.',
      time: 'Hace 2 horas',
      type: 'alert',
      read: false,
    },
    {
      id: '3',
      title: 'Planificación Inteligente Motion ⚡',
      message: 'Se han reajustado 3 tareas en tus huecos libres de hoy.',
      time: 'Hace 4 horas',
      type: 'task',
      read: true,
    },
    {
      id: '4',
      title: 'Racha de hábitos: 7 días 🎉',
      message: '¡Excelente disciplina con tu hábito de lectura y ejercicio!',
      time: 'Ayer',
      type: 'habit',
      read: true,
    },
  ]);

  const markAllAsRead = () => {
    setNotifications(notifications.map((n) => ({ ...n, read: true })));
  };

  return (
    <SafeAreaView className="flex-1 bg-slate-950 p-6">
      <View className="flex-row items-center justify-between mb-6">
        <TouchableOpacity onPress={() => router.back()} className="p-2 bg-slate-900 rounded-full">
          <Feather name="arrow-left" size={24} color="#f8fafc" />
        </TouchableOpacity>
        <Text className="text-xl font-bold text-white">Notificaciones & Alertas</Text>
        <TouchableOpacity onPress={markAllAsRead}>
          <Text className="text-xs text-indigo-400 font-semibold">Leer todas</Text>
        </TouchableOpacity>
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        {notifications.map((item) => (
          <View
            key={item.id}
            className={`p-4 rounded-2xl border mb-3 flex-row space-x-3 ${
              item.read ? 'bg-slate-900/60 border-slate-800' : 'bg-slate-900 border-indigo-500/40 shadow-sm'
            }`}
          >
            <View className="w-10 h-10 rounded-full bg-indigo-600/20 items-center justify-center mt-1">
              <Feather
                name={item.type === 'finance' ? 'dollar-sign' : item.type === 'task' ? 'check-circle' : 'bell'}
                size={20}
                color="#818cf8"
              />
            </View>
            <View className="flex-1">
              <View className="flex-row justify-between items-center">
                <Text className="text-white font-bold text-base flex-1">{item.title}</Text>
                <Text className="text-slate-500 text-xs">{item.time}</Text>
              </View>
              <Text className="text-slate-300 text-sm mt-1">{item.message}</Text>
            </View>
          </View>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
}
