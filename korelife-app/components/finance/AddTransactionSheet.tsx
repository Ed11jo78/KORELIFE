import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView, Modal, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import { X, ArrowDownLeft, ArrowUpRight, DollarSign, Wallet, Tag, Check, Calendar } from 'lucide-react-native';
import { useFinanceStore } from '../../src/store/financeStore';
import { useRatesStore } from '../../src/store/ratesStore';
import { CurrencyCode } from '../../src/types';

interface Props {
  visible: boolean;
  onClose: () => void;
}

export default function AddTransactionSheet({ visible, onClose }: Props) {
  const { accounts, addTransaction } = useFinanceStore();
  const { rates, selectedProvider } = useRatesStore();

  const [type, setType] = useState<'expense' | 'income' | 'transfer'>('expense');
  const [currency, setCurrency] = useState<CurrencyCode>('VES');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [selectedAccountId, setSelectedAccountId] = useState(accounts[0]?.id || '1');
  const [category, setCategory] = useState('Alimentación');

  const categories = [
    'Alimentación', 'Supermercado', 'Transporte', 'Servicios',
    'Salud & Farmacia', 'Educación', 'Entretenimiento', 'Nómina / Salario', 'Ventas / Negocio', 'Otros'
  ];

  const numericAmount = parseFloat(amount.replace(',', '.')) || 0;
  const activeRate = rates[selectedProvider]?.rate || 784.66;
  const activeRateName = rates[selectedProvider]?.name || 'BCV';

  // Conversión en vivo
  const equivalentInUsd = currency === 'VES' ? numericAmount / (activeRate || 1) : numericAmount;
  const equivalentInVes = currency === 'USD' ? numericAmount * activeRate : numericAmount;

  const handleSave = () => {
    if (numericAmount <= 0) {
      Alert.alert('Monto inválido', 'Por favor ingresa un monto mayor a 0');
      return;
    }

    addTransaction({
      account_id: selectedAccountId,
      user_id: '1',
      amount: numericAmount,
      currency: currency,
      type: type,
      category: category,
      description: description.trim() || category,
      date: new Date().toISOString().split('T')[0]
    });

    setAmount('');
    setDescription('');
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
        style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.88)' }}
      >
        <View 
          style={{ 
            backgroundColor: '#0f172a', 
            borderTopLeftRadius: 32, 
            borderTopRightRadius: 32, 
            borderTopWidth: 1.5, 
            borderColor: '#334155',
            maxHeight: '92%',
            paddingBottom: Platform.OS === 'ios' ? 34 : 20
          }}
          className="p-5"
        >
          {/* Barra de arrastre visual */}
          <View className="items-center mb-3">
            <View style={{ width: 44, height: 5, backgroundColor: '#475569', borderRadius: 10 }} />
          </View>

          {/* Header */}
          <View style={{ borderBottomColor: '#1e293b', borderBottomWidth: 1 }} className="flex-row justify-between items-center pb-3.5 mb-2">
            <View>
              <Text style={{ color: '#ffffff' }} className="text-xl font-black">Registrar Movimiento</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs mt-0.5 font-medium">Ingresa los detalles de tu transacción</Text>
            </View>
            <TouchableOpacity 
              onPress={onClose} 
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
              className="p-2.5 rounded-full"
              hitSlop={{ top: 12, bottom: 12, left: 12, right: 12 }}
            >
              <X size={18} color="#cbd5e1" />
            </TouchableOpacity>
          </View>

          <ScrollView showsVerticalScrollIndicator={false} className="py-2">
            {/* Tipo: Gasto / Ingreso */}
            <View style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }} className="flex-row rounded-2xl p-1.5 mb-4">
              <TouchableOpacity 
                onPress={() => setType('expense')}
                style={{ 
                  backgroundColor: type === 'expense' ? '#e11d48' : 'transparent',
                  borderColor: type === 'expense' ? '#f43f5e' : 'transparent',
                  borderWidth: type === 'expense' ? 1 : 0
                }}
                className="flex-1 py-3 rounded-xl items-center flex-row justify-center shadow-sm"
              >
                <ArrowDownLeft size={18} color={type === 'expense' ? '#ffffff' : '#94a3b8'} />
                <Text style={{ color: type === 'expense' ? '#ffffff' : '#94a3b8' }} className="font-black text-sm ml-2">
                  Gasto
                </Text>
              </TouchableOpacity>

              <TouchableOpacity 
                onPress={() => setType('income')}
                style={{ 
                  backgroundColor: type === 'income' ? '#059669' : 'transparent',
                  borderColor: type === 'income' ? '#10b981' : 'transparent',
                  borderWidth: type === 'income' ? 1 : 0
                }}
                className="flex-1 py-3 rounded-xl items-center flex-row justify-center shadow-sm"
              >
                <ArrowUpRight size={18} color={type === 'income' ? '#ffffff' : '#94a3b8'} />
                <Text style={{ color: type === 'income' ? '#ffffff' : '#94a3b8' }} className="font-black text-sm ml-2">
                  Ingreso
                </Text>
              </TouchableOpacity>
            </View>

            {/* Selector de Moneda */}
            <View className="mb-4">
              <Text style={{ color: '#cbd5e1' }} className="text-xs font-black mb-2 uppercase tracking-wider">
                Moneda del Registro
              </Text>
              <View className="flex-row space-x-2">
                <TouchableOpacity
                  onPress={() => setCurrency('VES')}
                  style={{
                    backgroundColor: currency === 'VES' ? '#06281e' : '#1e293b',
                    borderColor: currency === 'VES' ? '#10b981' : '#334155',
                    borderWidth: 1.5
                  }}
                  className="flex-1 py-3 rounded-2xl items-center shadow-sm mr-2"
                >
                  <Text style={{ color: currency === 'VES' ? '#34d399' : '#cbd5e1' }} className="font-black text-sm">
                    🇻🇪 Bolívares (Bs.)
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  onPress={() => setCurrency('USD')}
                  style={{
                    backgroundColor: currency === 'USD' ? '#06281e' : '#1e293b',
                    borderColor: currency === 'USD' ? '#10b981' : '#334155',
                    borderWidth: 1.5
                  }}
                  className="flex-1 py-3 rounded-2xl items-center shadow-sm"
                >
                  <Text style={{ color: currency === 'USD' ? '#34d399' : '#cbd5e1' }} className="font-black text-sm">
                    🇺🇸 Dólares ($)
                  </Text>
                </TouchableOpacity>
              </View>
            </View>

            {/* Input de Monto */}
            <View 
              style={{ backgroundColor: '#1e293b', borderColor: '#334155', borderWidth: 1 }}
              className="rounded-3xl p-5 mb-4 items-center shadow-lg"
            >
              <Text style={{ color: '#94a3b8' }} className="text-xs font-black uppercase mb-1">Monto a registrar</Text>
              <View className="flex-row items-center justify-center">
                <Text style={{ color: '#34d399' }} className="text-3xl font-black mr-2">
                  {currency === 'VES' ? 'Bs.' : '$'}
                </Text>
                <TextInput
                  value={amount}
                  onChangeText={setAmount}
                  placeholder="0.00"
                  placeholderTextColor="#64748b"
                  keyboardType="decimal-pad"
                  style={{ color: '#ffffff' }}
                  className="text-4xl font-black text-center min-w-[160px]"
                />
              </View>

              {/* Conversión en Tiempo Real */}
              {numericAmount > 0 && (
                <View 
                  style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
                  className="mt-3.5 px-4 py-2.5 rounded-2xl flex-row items-center justify-center w-full"
                >
                  <Text style={{ color: '#cbd5e1' }} className="text-xs font-medium">
                    Equivalente:{' '}
                    <Text style={{ color: '#34d399' }} className="font-black">
                      {currency === 'VES' 
                        ? `$ ${equivalentInUsd.toFixed(2)} USD` 
                        : `Bs. ${equivalentInVes.toLocaleString('es-VE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`}
                    </Text>
                  </Text>
                  <Text style={{ color: '#94a3b8' }} className="text-[10px] ml-1.5 font-bold">
                    (@ {activeRateName} {activeRate.toFixed(2)})
                  </Text>
                </View>
              )}
            </View>

            {/* Selector de Cuenta */}
            <View className="mb-4">
              <Text style={{ color: '#cbd5e1' }} className="text-xs font-black mb-2 uppercase tracking-wider">
                Cuenta de Origen
              </Text>
              <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-row -mx-1">
                {accounts.map((acc) => {
                  const isSel = selectedAccountId === acc.id;
                  return (
                    <TouchableOpacity
                      key={acc.id}
                      onPress={() => setSelectedAccountId(acc.id)}
                      style={{
                        backgroundColor: isSel ? '#1e1b4b' : '#1e293b',
                        borderColor: isSel ? '#818cf8' : '#334155',
                        borderWidth: isSel ? 1.5 : 1
                      }}
                      className="mr-2.5 px-4 py-3 rounded-2xl flex-row items-center shadow-sm"
                    >
                      <Wallet size={18} color={isSel ? '#a5b4fc' : '#94a3b8'} />
                      <View className="ml-2.5">
                        <Text style={{ color: isSel ? '#ffffff' : '#cbd5e1' }} className="text-xs font-black">
                          {acc.name}
                        </Text>
                        <Text style={{ color: '#94a3b8' }} className="text-[10px] font-bold">{acc.currency}</Text>
                      </View>
                    </TouchableOpacity>
                  );
                })}
              </ScrollView>
            </View>

            {/* Selector de Categoría */}
            <View className="mb-4">
              <Text style={{ color: '#cbd5e1' }} className="text-xs font-black mb-2 uppercase tracking-wider">
                Categoría
              </Text>
              <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-row -mx-1">
                {categories.map((cat) => {
                  const isSel = category === cat;
                  return (
                    <TouchableOpacity
                      key={cat}
                      onPress={() => setCategory(cat)}
                      style={{
                        backgroundColor: isSel ? '#06281e' : '#1e293b',
                        borderColor: isSel ? '#10b981' : '#334155',
                        borderWidth: isSel ? 1.5 : 1
                      }}
                      className="mr-2 px-4 py-2.5 rounded-xl shadow-sm"
                    >
                      <Text style={{ color: isSel ? '#34d399' : '#cbd5e1' }} className="text-xs font-bold">
                        {cat}
                      </Text>
                    </TouchableOpacity>
                  );
                })}
              </ScrollView>
            </View>

            {/* Descripción */}
            <View className="mb-5">
              <Text style={{ color: '#cbd5e1' }} className="text-xs font-black mb-2 uppercase tracking-wider">
                Descripción / Nota
              </Text>
              <TextInput
                value={description}
                onChangeText={setDescription}
                placeholder="Ej: Mercado semanal, almuerzo, gasolina..."
                placeholderTextColor="#64748b"
                style={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#ffffff', borderWidth: 1 }}
                className="p-4 rounded-2xl text-sm font-medium"
              />
            </View>

            {/* Botón Guardar */}
            <TouchableOpacity
              onPress={handleSave}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-2xl items-center shadow-2xl active:bg-emerald-600 mb-4"
            >
              <Text style={{ color: '#022c22' }} className="font-black text-base">Guardar Movimiento ✅</Text>
            </TouchableOpacity>
          </ScrollView>
        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}