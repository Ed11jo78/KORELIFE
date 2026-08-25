import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, RefreshControl } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowDownLeft, ArrowUpRight, Plus, Wallet, PieChart, ArrowRight, RefreshCw, Target, Repeat, Gem } from 'lucide-react-native';
import AccountCard from '../../../components/finance/AccountCard';
import TransactionItem from '../../../components/finance/TransactionItem';
import ExchangeRatesCard from '../../../components/finance/ExchangeRatesCard';
import AddTransactionSheet from '../../../components/finance/AddTransactionSheet';
import { useFinanceStore } from '../../../src/store/financeStore';
import { useRatesStore } from '../../../src/store/ratesStore';

export default function FinanceIndexScreen() {
  const router = useRouter();
  const { accounts, transactions, getTotalBalanceInVes, getTotalBalanceInUsd, fetchData } = useFinanceStore();
  const { formatCurrency, fetchRates, rates, selectedProvider } = useRatesStore();

  const [modalVisible, setModalVisible] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const onRefresh = async () => {
    setRefreshing(true);
    await Promise.all([fetchData(), fetchRates()]);
    setRefreshing(false);
  };

  const totalVes = getTotalBalanceInVes();
  const totalUsd = getTotalBalanceInUsd();
  const activeRate = rates[selectedProvider];

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }}>
      <ScrollView 
        className="flex-1 px-4" 
        showsVerticalScrollIndicator={false}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} tintColor="#10b981" />}
      >
        {/* Cabecera */}
        <View className="pt-3 pb-4 flex-row justify-between items-center">
          <View>
            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold uppercase tracking-wider">Módulo Financiero</Text>
            <Text style={{ color: '#ffffff' }} className="text-2xl font-black">Mis Finanzas 💼</Text>
          </View>
          <TouchableOpacity
            onPress={() => setModalVisible(true)}
            style={{ backgroundColor: '#10b981' }}
            className="p-3 rounded-full shadow-xl active:bg-emerald-600"
          >
            <Plus size={20} color="#022c22" strokeWidth={2.5} />
          </TouchableOpacity>
        </View>

        {/* Resumen de Patrimonio */}
        <View 
          style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
          className="rounded-3xl p-5 mb-5 shadow-xl"
        >
          <View className="flex-row justify-between items-center mb-1.5">
            <Text style={{ color: '#94a3b8' }} className="text-xs font-black uppercase tracking-wider">Patrimonio Neto Total</Text>
            <View style={{ backgroundColor: '#1e293b', borderColor: '#334155' }} className="px-2.5 py-1 rounded-xl border">
              <Text style={{ color: '#34d399' }} className="text-[10px] font-black">1 USD = {activeRate.rate.toFixed(2)} Bs.</Text>
            </View>
          </View>

          <Text style={{ color: '#ffffff' }} className="text-3xl font-black tracking-tight">
            {formatCurrency(totalVes, 'VES')}
          </Text>

          <Text style={{ color: '#34d399' }} className="text-base font-black mt-0.5">
            ≈ {formatCurrency(totalUsd, 'USD')} USD
          </Text>

          {/* Botones Ingreso / Gasto */}
          <View className="flex-row space-x-3 mt-4">
            <TouchableOpacity 
              onPress={() => setModalVisible(true)}
              style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981', borderWidth: 1 }}
              className="flex-1 py-3 rounded-2xl flex-row items-center justify-center shadow-sm active:opacity-80"
            >
              <ArrowDownLeft size={18} color="#34d399" />
              <Text style={{ color: '#34d399' }} className="font-black ml-1.5 text-xs">+ Ingreso</Text>
            </TouchableOpacity>

            <TouchableOpacity 
              onPress={() => setModalVisible(true)}
              style={{ backgroundColor: 'rgba(244, 63, 94, 0.15)', borderColor: '#f43f5e', borderWidth: 1 }}
              className="flex-1 py-3 rounded-2xl flex-row items-center justify-center shadow-sm active:opacity-80"
            >
              <ArrowUpRight size={18} color="#fb7185" />
              <Text style={{ color: '#fb7185' }} className="font-black ml-1.5 text-xs">- Gasto</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Acceso Rápido a Secciones Financieras */}
        <View className="flex-row gap-2 mb-5">
          <TouchableOpacity 
            onPress={() => router.push('/finance/goals')}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b' }}
            className="flex-1 p-3 rounded-2xl border items-center shadow-sm"
          >
            <Target size={18} color="#10b981" />
            <Text style={{ color: '#ffffff' }} className="text-xs font-bold mt-1">Metas</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            onPress={() => router.push('/finance/subscriptions')}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b' }}
            className="flex-1 p-3 rounded-2xl border items-center shadow-sm"
          >
            <Repeat size={18} color="#818cf8" />
            <Text style={{ color: '#ffffff' }} className="text-xs font-bold mt-1">Suscrip.</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            onPress={() => router.push('/finance/wealth')}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b' }}
            className="flex-1 p-3 rounded-2xl border items-center shadow-sm"
          >
            <Gem size={18} color="#f59e0b" />
            <Text style={{ color: '#ffffff' }} className="text-xs font-bold mt-1">Patrimonio</Text>
          </TouchableOpacity>
          <TouchableOpacity 
            onPress={() => router.push('/finance/analytics')}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b' }}
            className="flex-1 p-3 rounded-2xl border items-center shadow-sm"
          >
            <PieChart size={18} color="#38bdf8" />
            <Text style={{ color: '#ffffff' }} className="text-xs font-bold mt-1">Analítica</Text>
          </TouchableOpacity>
        </View>

        {/* Monitor de Tasas */}
        <ExchangeRatesCard />

        {/* Cuentas Bancarias y Efectivo */}
        <View className="mb-6">
          <View className="flex-row justify-between items-center mb-3">
            <Text style={{ color: '#ffffff' }} className="text-lg font-black">Cuentas & Billeteras</Text>
            <Text style={{ color: '#94a3b8' }} className="text-xs font-bold">{accounts.length} activas</Text>
          </View>

          <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-row -mx-4 px-4 pb-2">
            {accounts.map((acc) => (
              <AccountCard
                key={acc.id}
                name={acc.name}
                balance={acc.balance}
                type={acc.type}
                currency={acc.currency}
              />
            ))}
          </ScrollView>
        </View>

        {/* Últimos Movimientos */}
        <View className="mb-10">
          <View className="flex-row justify-between items-center mb-3">
            <Text style={{ color: '#ffffff' }} className="text-lg font-black">Últimos Movimientos</Text>
            <TouchableOpacity 
              onPress={() => router.push('/finance/transactions')}
              className="flex-row items-center"
            >
              <Text style={{ color: '#34d399' }} className="text-xs font-bold mr-1">Ver todos</Text>
              <ArrowRight size={14} color="#34d399" />
            </TouchableOpacity>
          </View>

          {transactions.length === 0 ? (
            <Text style={{ color: '#64748b' }} className="text-xs italic py-4 text-center font-medium">
              No hay movimientos registrados.
            </Text>
          ) : (
            transactions.slice(0, 5).map((tx) => (
              <TransactionItem key={tx.id} transaction={tx} />
            ))
          )}
        </View>

        <View className="h-10" />
      </ScrollView>

      {/* Modal de Transacciones */}
      <AddTransactionSheet 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
      />
    </SafeAreaView>
  );
}