import React, { useEffect, useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, RefreshControl } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Plus, CheckCircle2, Circle, User, Wallet, Sparkles, TrendingUp, ShieldCheck } from 'lucide-react-native';
import { useAuthStore } from '../../src/store/authStore';
import { useFinanceStore } from '../../src/store/financeStore';
import { useTasksStore } from '../../src/store/tasksStore';
import { useRatesStore } from '../../src/store/ratesStore';
import ExchangeRatesCard from '../../components/finance/ExchangeRatesCard';
import AddTransactionSheet from '../../components/finance/AddTransactionSheet';

export default function DashboardScreen() {
  const { user } = useAuthStore();
  const { accounts, fetchData, getTotalBalanceInVes, getTotalBalanceInUsd } = useFinanceStore();
  const { tasks, habits, fetchTasks } = useTasksStore();
  const { formatCurrency, fetchRates, rates, selectedProvider } = useRatesStore();

  const [modalVisible, setModalVisible] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchData();
    fetchTasks();
    fetchRates();
  }, []);

  const onRefresh = async () => {
    setRefreshing(true);
    await Promise.all([fetchData(), fetchTasks(), fetchRates()]);
    setRefreshing(false);
  };

  const totalVes = getTotalBalanceInVes();
  const totalUsd = getTotalBalanceInUsd();
  const activeRate = rates[selectedProvider];

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }}>
      <ScrollView 
        className="p-4" 
        showsVerticalScrollIndicator={false}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#10b981" />}
      >
        {/* Header de Bienvenida */}
        <View className="flex-row justify-between items-center mb-5">
          <View>
            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold uppercase tracking-wider">Bienvenido de nuevo</Text>
            <Text style={{ color: '#ffffff' }} className="text-2xl font-black">
              {user?.full_name || user?.email?.split('@')[0] || 'KoreLife User'} 👋
            </Text>
          </View>
          <View style={{ backgroundColor: '#064e3b', borderColor: '#10b981' }} className="w-12 h-12 border rounded-2xl items-center justify-center shadow-lg">
            <User size={24} color="#34d399" />
          </View>
        </View>

        {/* Monitor de Tasas de Cambio en Vivo */}
        <ExchangeRatesCard />

        {/* Card Principal: Patrimonio Total en Bolívares y Dólares */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
          className="rounded-3xl p-5 mb-5 shadow-2xl"
        >
          <View className="flex-row justify-between items-center mb-2">
            <View className="flex-row items-center">
              <Wallet size={18} color="#818cf8" />
              <Text style={{ color: '#c7d2fe' }} className="text-xs font-black ml-1.5 uppercase tracking-wider">
                Patrimonio Total
              </Text>
            </View>
            <View style={{ backgroundColor: '#1e1b4b', borderColor: '#6366f1' }} className="px-2.5 py-1 rounded-xl border">
              <Text style={{ color: '#a5b4fc' }} className="text-[10px] font-black">Tasa: {activeRate.name}</Text>
            </View>
          </View>

          {/* Monto Grande en Bolívares */}
          <Text style={{ color: '#ffffff' }} className="text-3xl font-black tracking-tight">
            {formatCurrency(totalVes, 'VES')}
          </Text>

          {/* Equivalente en Dólares */}
          <View className="flex-row items-center mt-1 mb-4">
            <Text style={{ color: '#34d399' }} className="font-black text-lg">
              ≈ {formatCurrency(totalUsd, 'USD')}
            </Text>
            <Text style={{ color: '#94a3b8' }} className="text-xs ml-1.5 font-bold">USD</Text>
          </View>

          {/* Barra de salud financiera */}
          <View style={{ backgroundColor: '#1e293b' }} className="w-full h-3 rounded-full overflow-hidden mb-2">
            <View style={{ backgroundColor: '#10b981', width: '75%' }} className="h-full rounded-full" />
          </View>
          
          <View className="flex-row justify-between items-center">
            <Text style={{ color: '#94a3b8' }} className="text-xs font-medium">Disponible para gastar este mes</Text>
            <Text style={{ color: '#34d399' }} className="text-xs font-black">75% saludable ✅</Text>
          </View>
        </View>

        {/* Acciones Rápidas */}
        <View className="mb-5">
          <TouchableOpacity 
            onPress={() => setModalVisible(true)}
            style={{ backgroundColor: '#10b981' }}
            className="py-4 px-4 rounded-2xl flex-row items-center justify-center shadow-xl active:bg-emerald-600"
          >
            <Plus size={20} color="#022c22" strokeWidth={3} />
            <Text style={{ color: '#022c22' }} className="font-black ml-2 text-base">Registrar Gasto / Ingreso</Text>
          </TouchableOpacity>
        </View>

        {/* Tareas de Hoy */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
          className="rounded-3xl p-5 mb-5 shadow-lg"
        >
          <View className="flex-row justify-between items-center mb-3.5">
            <Text style={{ color: '#ffffff' }} className="font-black text-base">Tareas para Hoy</Text>
            <View style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981' }} className="px-2.5 py-0.5 rounded-full border">
              <Text style={{ color: '#34d399' }} className="text-xs font-bold">
                {tasks.filter(t => t.completed).length} / {tasks.length} listas
              </Text>
            </View>
          </View>

          {tasks.length === 0 ? (
            <Text style={{ color: '#64748b' }} className="text-xs italic py-2">No tienes tareas pendientes para hoy.</Text>
          ) : (
            tasks.slice(0, 4).map((task) => (
              <View 
                key={task.id} 
                style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
                className="p-3.5 rounded-2xl mb-2 flex-row items-center border"
              >
                {task.completed ? (
                  <CheckCircle2 size={20} color="#34d399" />
                ) : (
                  <Circle size={20} color="#64748b" />
                )}
                <Text 
                  style={{ color: task.completed ? '#64748b' : '#ffffff', textDecorationLine: task.completed ? 'line-through' : 'none' }}
                  className="ml-3 text-sm font-semibold flex-1"
                >
                  {task.title}
                </Text>
              </View>
            ))
          )}
        </View>

        {/* Hábitos del Día */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
          className="rounded-3xl p-5 mb-8 shadow-lg"
        >
          <View className="flex-row justify-between items-center mb-3">
            <Text style={{ color: '#ffffff' }} className="font-black text-base">Hábitos Activos</Text>
            <Sparkles size={18} color="#fbbf24" />
          </View>
          <View className="flex-row flex-wrap justify-between">
            <View 
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', width: '48%' }}
              className="p-3.5 rounded-2xl mb-2 border"
            >
              <Text style={{ color: '#fbbf24' }} className="text-xs font-black mb-1">💧 Tomar 2L de agua</Text>
              <Text style={{ color: '#94a3b8' }} className="text-[11px] font-bold">Racha: 7 días 🔥</Text>
            </View>
            <View 
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', width: '48%' }}
              className="p-3.5 rounded-2xl mb-2 border"
            >
              <Text style={{ color: '#34d399' }} className="text-xs font-black mb-1">📖 Leer 20 páginas</Text>
              <Text style={{ color: '#94a3b8' }} className="text-[11px] font-bold">Racha: 14 días 🔥</Text>
            </View>
          </View>
        </View>

        {/* Espacio inferior */}
        <View className="h-16" />
      </ScrollView>

      {/* FAB Flotante */}
      <TouchableOpacity 
        onPress={() => setModalVisible(true)}
        style={{ backgroundColor: '#10b981' }}
        className="absolute bottom-6 right-6 w-14 h-14 rounded-full items-center justify-center shadow-2xl active:bg-emerald-600"
      >
        <Plus size={28} color="#022c22" strokeWidth={3} />
      </TouchableOpacity>

      {/* Modal de Registro de Transacción */}
      <AddTransactionSheet 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
      />
    </SafeAreaView>
  );
}