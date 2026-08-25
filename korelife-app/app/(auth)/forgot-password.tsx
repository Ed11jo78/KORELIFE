import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Mail, ArrowLeft, KeyRound, CheckCircle } from 'lucide-react-native';
import { supabase } from '../../src/lib/supabase';

export default function ForgotPasswordScreen() {
  const router = useRouter();
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [sent, setSent] = useState(false);

  const handleReset = async () => {
    if (!email.trim()) {
      Alert.alert('Correo requerido', 'Por favor ingresa tu correo electrónico.');
      return;
    }

    setLoading(true);
    const { error } = await supabase.auth.resetPasswordForEmail(email.trim());
    setLoading(false);

    if (error) {
      Alert.alert('Error', error.message);
    } else {
      setSent(true);
    }
  };

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-6 justify-between py-6">
      <View>
        <TouchableOpacity 
          onPress={() => router.back()} 
          className="p-2 bg-slate-900 rounded-full w-10 h-10 items-center justify-center mb-6 border border-slate-800"
        >
          <ArrowLeft size={20} color="white" />
        </TouchableOpacity>

        <View className="w-14 h-14 bg-indigo-500/20 border border-indigo-500/30 rounded-2xl items-center justify-center mb-4">
          <KeyRound size={28} color="#818cf8" />
        </View>

        <Text className="text-white text-3xl font-black">Recuperar Contraseña</Text>
        <Text className="text-slate-400 text-sm mt-1.5 mb-8">
          Ingresa el correo vinculado a tu cuenta para enviarte las instrucciones de restablecimiento.
        </Text>

        {sent ? (
          <View className="bg-emerald-950/40 p-5 rounded-2xl border border-emerald-500/40 items-center text-center">
            <CheckCircle size={36} color="#10b981" />
            <Text className="text-white font-bold text-base mt-2">¡Enlace Enviado!</Text>
            <Text className="text-slate-300 text-xs text-center mt-1">
              Revisa tu bandeja de entrada en <Text className="font-bold text-emerald-400">{email}</Text> para restablecer tu contraseña.
            </Text>
          </View>
        ) : (
          <View className="space-y-4">
            <View>
              <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Correo Electrónico</Text>
              <View className="flex-row items-center bg-slate-900 px-4 py-3.5 rounded-2xl border border-slate-800">
                <Mail size={18} color="#64748b" />
                <TextInput
                  value={email}
                  onChangeText={setEmail}
                  placeholder="tu@correo.com"
                  placeholderTextColor="#64748b"
                  autoCapitalize="none"
                  keyboardType="email-address"
                  className="flex-1 ml-3 text-white text-sm"
                />
              </View>
            </View>

            <TouchableOpacity
              onPress={handleReset}
              disabled={loading}
              className="bg-emerald-500 py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600 mt-2"
            >
              {loading ? (
                <ActivityIndicator color="#022c22" />
              ) : (
                <Text className="text-slate-950 font-black text-base">Enviar Enlace de Recuperación</Text>
              )}
            </TouchableOpacity>
          </View>
        )}
      </View>

      <TouchableOpacity onPress={() => router.push('/(auth)/login')} className="items-center mb-4">
        <Text className="text-slate-400 text-sm">
          ¿Recordaste tu clave? <Text className="text-emerald-400 font-bold">Inicia Sesión</Text>
        </Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}