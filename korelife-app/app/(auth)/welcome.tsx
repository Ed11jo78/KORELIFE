import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, ActivityIndicator, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Sparkles, ArrowRight, ShieldCheck, TrendingUp, CheckCircle, Zap } from 'lucide-react-native';
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

export default function WelcomeScreen() {
  const router = useRouter();
  const { setSession } = useAuthStore();
  const [googleLoading, setGoogleLoading] = useState(false);

  const handleGoogleLogin = async () => {
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
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-6 justify-between py-6">
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Cabecera & Logo */}
        <View className="items-center mt-4 mb-6">
          <View style={{ backgroundColor: '#064e3b', borderColor: '#10b981', borderWidth: 2 }} className="w-20 h-20 rounded-3xl items-center justify-center mb-3 shadow-2xl">
            <Zap size={40} color="#34d399" />
          </View>
          <Text style={{ color: '#ffffff' }} className="text-4xl font-black tracking-tight">KoreLife</Text>
          <View style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981' }} className="flex-row items-center px-3.5 py-1 rounded-full mt-2 border">
            <Sparkles size={12} color="#34d399" />
            <Text style={{ color: '#34d399' }} className="text-xs font-black ml-1.5 uppercase">Finanzas & Productividad Pro</Text>
          </View>
        </View>

        {/* Características Destacadas */}
        <View className="space-y-3 mb-6">
          <View style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }} className="p-4 rounded-2xl flex-row items-center mb-2.5 shadow-sm">
            <View style={{ backgroundColor: '#064e3b', borderColor: '#10b981' }} className="w-10 h-10 rounded-xl items-center justify-center mr-3.5 border">
              <TrendingUp size={20} color="#34d399" />
            </View>
            <View className="flex-1">
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm">Tasas en Tiempo Real</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs mt-0.5">Monitorea BCV Dólar/Euro, Binance P2P y Airtm al instante.</Text>
            </View>
          </View>

          <View style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }} className="p-4 rounded-2xl flex-row items-center mb-2.5 shadow-sm">
            <View style={{ backgroundColor: '#1e1b4b', borderColor: '#6366f1' }} className="w-10 h-10 rounded-xl items-center justify-center mr-3.5 border">
              <CheckCircle size={20} color="#818cf8" />
            </View>
            <View className="flex-1">
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm">Gestión Multimoneda en Bolívares</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs mt-0.5">Registra movimientos en Bs. con conversión automática a USD.</Text>
            </View>
          </View>

          <View style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }} className="p-4 rounded-2xl flex-row items-center mb-2.5 shadow-sm">
            <View style={{ backgroundColor: '#082f49', borderColor: '#0284c7' }} className="w-10 h-10 rounded-xl items-center justify-center mr-3.5 border">
              <ShieldCheck size={20} color="#38bdf8" />
            </View>
            <View className="flex-1">
              <Text style={{ color: '#ffffff' }} className="font-bold text-sm">Productividad GTD & Hábitos</Text>
              <Text style={{ color: '#94a3b8' }} className="text-xs mt-0.5">Tu cockpit diario para cumplir metas y organizar tu día.</Text>
            </View>
          </View>
        </View>

        {/* Botones de Acción */}
        <View className="space-y-3 mb-6">
          {/* Botón de Google OAuth */}
          <TouchableOpacity
            onPress={handleGoogleLogin}
            disabled={googleLoading}
            style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
            className="py-4 px-4 rounded-2xl flex-row items-center justify-center shadow-lg active:bg-slate-800 mb-2.5"
          >
            {googleLoading ? (
              <ActivityIndicator color="#34d399" />
            ) : (
              <>
                <GoogleIcon />
                <Text style={{ color: '#ffffff' }} className="font-bold text-base ml-3">
                  Continuar con Google
                </Text>
              </>
            )}
          </TouchableOpacity>

          <TouchableOpacity 
            style={{ backgroundColor: '#10b981' }}
            className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600 flex-row justify-center mb-2.5"
            onPress={() => router.push('/(auth)/login')}
          >
            <Text style={{ color: '#022c22' }} className="font-black text-base mr-2">Iniciar Sesión con Correo</Text>
            <ArrowRight size={18} color="#022c22" strokeWidth={2.5} />
          </TouchableOpacity>

          <TouchableOpacity 
            style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
            className="py-4 rounded-2xl items-center active:bg-slate-800 mb-3"
            onPress={() => router.push('/(auth)/register')}
          >
            <Text style={{ color: '#ffffff' }} className="font-bold text-base">Crear Cuenta Nueva</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            className="py-2 items-center"
            onPress={() => router.replace('/(tabs)')}
          >
            <Text style={{ color: '#94a3b8' }} className="text-xs font-semibold">Explorar en Modo Rápido / Demo</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}