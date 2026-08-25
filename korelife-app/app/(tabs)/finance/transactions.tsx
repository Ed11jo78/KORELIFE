import React, { useState } from 'react';
import { View, Text, ScrollView, TouchableOpacity, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { ArrowLeft, Search, Plus, Filter, X } from 'lucide-react-native';
import TransactionItem from '../../../components/finance/TransactionItem';
import AddTransactionSheet from '../../../components/finance/AddTransactionSheet';
import { useFinanceStore } from '../../../src/store/financeStore';

export default function TransactionsScreen() {
  const router = useRouter();
  const { transactions } = useFinanceStore();
  const [search, setSearch] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [filterType, setFilterType] = useState<'all' | 'income' | 'expense'>('all');

  const filtered = transactions.filter((tx) => {
    const matchesSearch = tx.description.toLowerCase().includes(search.toLowerCase()) || 
                          tx.category.toLowerCase().includes(search.toLowerCase());
    const matchesType = filterType === 'all' ? true : tx.type === filterType;
    return matchesSearch && matchesType;
  });

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
        <Text style={{ color: '#ffffff' }} className="text-lg font-black">Historial de Transacciones</Text>
        <TouchableOpacity 
          onPress={() => setModalVisible(true)} 
          style={{ backgroundColor: '#10b981' }}
          className="p-2.5 rounded-full shadow-lg active:bg-emerald-600"
        >
          <Plus size={20} color="#022c22" strokeWidth={2.5} />
        </TouchableOpacity>
      </View>

      {/* Buscador */}
      <View 
        style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
        className="flex-row items-center px-4 py-3 rounded-2xl border mb-3 shadow-sm"
      >
        <Search size={18} color="#94a3b8" />
        <TextInput
          value={search}
          onChangeText={setSearch}
          placeholder="Buscar por concepto o categoría..."
          placeholderTextColor="#64748b"
          style={{ color: '#ffffff' }}
          className="flex-1 ml-2.5 text-sm font-medium"
        />
        {search.length > 0 && (
          <TouchableOpacity onPress={() => setSearch('')}>
            <X size={16} color="#94a3b8" />
          </TouchableOpacity>
        )}
      </View>

      {/* Filtros rápidos */}
      <View className="flex-row space-x-2 mb-4">
        {(['all', 'expense', 'income'] as const).map((f) => {
          const isSel = filterType === f;
          return (
            <TouchableOpacity
              key={f}
              onPress={() => setFilterType(f)}
              style={{
                backgroundColor: isSel ? '#06281e' : '#0f172a',
                borderColor: isSel ? '#10b981' : '#334155',
                borderWidth: isSel ? 1.5 : 1
              }}
              className="px-4 py-2 rounded-xl shadow-sm mr-2"
            >
              <Text 
                style={{ color: isSel ? '#34d399' : '#94a3b8' }} 
                className="text-xs font-bold"
              >
                {f === 'all' ? 'Todos' : f === 'expense' ? 'Gastos' : 'Ingresos'}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>

      {/* Lista */}
      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        {filtered.length === 0 ? (
          <View className="items-center py-16">
            <Text className="text-4xl mb-2">🔍</Text>
            <Text style={{ color: '#94a3b8' }} className="text-sm font-bold text-center">
              No se encontraron transacciones.
            </Text>
          </View>
        ) : (
          filtered.map((tx) => (
            <TransactionItem key={tx.id} transaction={tx} />
          ))
        )}
        <View className="h-10" />
      </ScrollView>

      <AddTransactionSheet 
        visible={modalVisible} 
        onClose={() => setModalVisible(false)} 
      />
    </SafeAreaView>
  );
}