import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Mail, Lock, User, ArrowLeft, Check, Sparkles } from 'lucide-react-native';
import { supabase } from '../../src/lib/supabase';
import { CurrencyCode } from '../../src/types';
import { useAuthStore } from '../../src/store/authStore';
import { signInWithGoogle } from '../../src/lib/authGoogle';
import Svg, { Path } from 'react-native-svg';

function GoogleIcon() {
  return (
    <Svg width={20} height={20} viewBox="0 0 24 24">
      <Path
        fill="#EA4335"
        d="M12 5c1.6 0 3 .6 4.1 1.7l3.1-3.1C17.3 1.8 14.8 1 12 1 7.5 1 3.7 3.6 1.9 7.3l3.7 2.9C6.5 7.4 9 5 12 5z"
      />
      <Path
        fill="#4285F4"
        d="M23.5 12.3c0-.8-.1-1.7-.2-2.3H12v4.6h6.5c-.3 1.5-1.1 2.8-2.4 3.7l3.7 2.9c2.2-2 3.7-5 3.7-8.9z"
      />
      <Path
        fill="#FBBC05"
        d="M5.6 14.8c-.3-.8-.4-1.8-.4-2.8 0-1 .1-2 .4-2.8L1.9 6.3C.7 8.7 0 10.3 0 12s.7 3.3 1.9 5.7l3.7-2.9z"
      />
      <Path
        fill="#34A853"
        d="M12 23c3.2 0 6-1.1 8-3l-3.7-2.9c-1.1.7-2.5 1.2-4.3 1.2-3 0-5.5-2.4-6.4-5.2L1.9 16C3.7 19.7 7.5 23 12 23z"
      />
    </Svg>
  );
}

export default function RegisterScreen() {
  const router = useRouter();
  const { setSession } = useAuthStore();
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [currency, setCurrency] = useState<CurrencyCode>('VES');
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  const currencies: { code: CurrencyCode; label: string; flag: string; desc: string }[] = [
    { code: 'VES', label: 'Bolívares', flag: '🇻🇪', desc: 'Moneda local con tasas BCV/Binance' },
    { code: 'USD', label: 'Dólares', flag: '🇺🇸', desc: 'Dólar estadounidense ($)' },
    { code: 'EUR', label: 'Euros', flag: '🇪🇺', desc: 'Euro (€)' }
  ];

  const handleRegister = async () => {
    if (!email.trim() || !password || !fullName.trim()) {
      Alert.alert('Campos requeridos', 'Por favor llena todos los campos para continuar.');
      return;
    }

    setLoading(true);
    const { data, error } = await supabase.auth.signUp({
      email: email.trim(),
      password: password,
      options: {
        data: {
          full_name: fullName.trim(),
          currency: currency
        }
      }
    });

    setLoading(false);
    if (error) {
      Alert.alert('Error al registrarse', error.message);
    } else {
      if (data.session) {
        setSession(data.session);
      }
      Alert.alert('¡Cuenta creada!', 'Tu cuenta ha sido creada exitosamente.', [
        { text: 'Comenzar', onPress: () => router.replace('/(tabs)') }
      ]);
    }
  };

  const handleGoogleRegister = async () => {
    setGoogleLoading(true);
    const res = await signInWithGoogle();
    setGoogleLoading(false);

    if (res.success && res.session) {
      setSession(res.session);
      router.replace('/(tabs)');
    } else if (res.error) {
      Alert.alert('Google Sign-In', res.error);
    }
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-5">
      <ScrollView showsVerticalScrollIndicator={false} className="py-4">
        <TouchableOpacity 
          onPress={() => router.back()} 
          style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
          className="p-2 rounded-full w-10 h-10 items-center justify-center mb-4 border"
        >
          <ArrowLeft size={20} color="white" />
        </TouchableOpacity>

        <Text style={{ color: '#ffffff' }} className="text-3xl font-black mb-1">Crea tu Cuenta</Text>
        <Text style={{ color: '#94a3b8' }} className="text-sm mb-6">Únete a KoreLife y gestiona tus finanzas y tareas de forma inteligente.</Text>

        {/* Botón de Google OAuth */}
        <TouchableOpacity
          onPress={handleGoogleRegister}
          disabled={googleLoading}
          style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
          className="py-3.5 px-4 rounded-2xl flex-row items-center justify-center shadow-md active:bg-slate-800 mb-4"
        >
          {googleLoading ? (
            <ActivityIndicator color="#34d399" />
          ) : (
            <>
              <GoogleIcon />
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm ml-3">
                Registrarse con Google
              </Text>
            </>
          )}
        </TouchableOpacity>

        {/* Divisor con texto */}
        <View className="flex-row items-center mb-4">
          <View style={{ backgroundColor: '#1e293b' }} className="flex-1 h-px" />
          <Text style={{ color: '#64748b' }} className="text-xs font-bold uppercase mx-3">O completa tus datos</Text>
          <View style={{ backgroundColor: '#1e293b' }} className="flex-1 h-px" />
        </View>

        {/* Nombre completo */}
        <View className="mb-4">
          <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-1.5 uppercase">Nombre Completo</Text>
          <View style={{ backgroundColor: '#0f172a', borderColor: '#334155' }} className="flex-row items-center px-4 py-3.5 rounded-2xl border">
            <User size={18} color="#94a3b8" />
            <TextInput
              value={fullName}
              onChangeText={setFullName}
              placeholder="Ej: Carlos Pérez"
              placeholderTextColor="#64748b"
              style={{ color: '#ffffff' }}
              className="flex-1 ml-3 text-sm font-medium"
            />
          </View>
        </View>

        {/* Correo Electrónico */}
        <View className="mb-4">
          <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-1.5 uppercase">Correo Electrónico</Text>
          <View style={{ backgroundColor: '#0f172a', borderColor: '#334155' }} className="flex-row items-center px-4 py-3.5 rounded-2xl border">
            <Mail size={18} color="#94a3b8" />
            <TextInput
              value={email}
              onChangeText={setEmail}
              placeholder="tu@correo.com"
              placeholderTextColor="#64748b"
              autoCapitalize="none"
              keyboardType="email-address"
              style={{ color: '#ffffff' }}
              className="flex-1 ml-3 text-sm font-medium"
            />
          </View>
        </View>

        {/* Contraseña */}
        <View className="mb-5">
          <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-1.5 uppercase">Contraseña</Text>
          <View style={{ backgroundColor: '#0f172a', borderColor: '#334155' }} className="flex-row items-center px-4 py-3.5 rounded-2xl border">
            <Lock size={18} color="#94a3b8" />
            <TextInput
              value={password}
              onChangeText={setPassword}
              placeholder="••••••••"
              placeholderTextColor="#64748b"
              secureTextEntry
              style={{ color: '#ffffff' }}
              className="flex-1 ml-3 text-sm font-medium"
            />
          </View>
        </View>

        {/* Selector de Moneda Principal */}
        <View className="mb-6">
          <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-1.5 uppercase">Moneda Principal Preferida</Text>
          <Text style={{ color: '#94a3b8' }} className="text-xs mb-3">Podrás registrar y ver tus saldos en esta moneda con conversión en vivo.</Text>

          {currencies.map((curr) => {
            const isSel = currency === curr.code;
            return (
              <TouchableOpacity
                key={curr.code}
                onPress={() => setCurrency(curr.code)}
                style={{
                  backgroundColor: isSel ? '#06281e' : '#0f172a',
                  borderColor: isSel ? '#10b981' : '#334155',
                  borderWidth: isSel ? 1.5 : 1
                }}
                className="p-3.5 rounded-2xl mb-2.5 flex-row items-center justify-between shadow-sm"
              >
                <View className="flex-row items-center">
                  <Text className="text-2xl mr-3">{curr.flag}</Text>
                  <View>
                    <Text style={{ color: isSel ? '#34d399' : '#ffffff' }} className="font-bold text-sm">
                      {curr.label} ({curr.code})
                    </Text>
                    <Text style={{ color: '#94a3b8' }} className="text-xs">{curr.desc}</Text>
                  </View>
                </View>

                {isSel && (
                  <View style={{ backgroundColor: '#10b981' }} className="w-6 h-6 rounded-full items-center justify-center">
                    <Check size={14} color="#022c22" strokeWidth={3} />
                  </View>
                )}
              </TouchableOpacity>
            );
          })}
        </View>

        {/* Botón de Registro */}
        <TouchableOpacity
          onPress={handleRegister}
          disabled={loading}
          style={{ backgroundColor: '#10b981' }}
          className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600 mb-6"
        >
          {loading ? (
            <ActivityIndicator color="#022c22" />
          ) : (
            <Text style={{ color: '#022c22' }} className="font-black text-base">Crear Mi Cuenta</Text>
          )}
        </TouchableOpacity>

        {/* Link a Login */}
        <TouchableOpacity onPress={() => router.push('/(auth)/login')} className="mb-10 items-center">
          <Text style={{ color: '#94a3b8' }} className="text-sm">
            ¿Ya tienes una cuenta? <Text style={{ color: '#34d399' }} className="font-black">Inicia Sesión</Text>
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}