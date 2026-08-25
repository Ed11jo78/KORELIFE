import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, Alert, ActivityIndicator, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Mail, Lock, LogIn, ArrowRight, Eye, EyeOff, Sparkles, ArrowLeft } from 'lucide-react-native';
import { supabase } from '../../src/lib/supabase';
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

export default function LoginScreen() {
  const router = useRouter();
  const { setSession } = useAuthStore();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password) {
      Alert.alert('Datos requeridos', 'Por favor ingresa tu correo y contraseña.');
      return;
    }

    setLoading(true);
    const { data, error } = await supabase.auth.signInWithPassword({
      email: email.trim(),
      password: password
    });

    setLoading(false);
    if (error) {
      Alert.alert('Error de acceso', error.message);
    } else {
      setSession(data.session);
      router.replace('/(tabs)');
    }
  };

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

  const handleQuickDemo = () => {
    router.replace('/(tabs)');
  };

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#020617' }} className="px-6 justify-between py-4">
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={{ flexGrow: 1, justifyContent: 'space-between' }}>
        <View>
          <TouchableOpacity 
            onPress={() => router.back()} 
            style={{ backgroundColor: '#0f172a', borderColor: '#334155' }}
            className="p-2 rounded-full w-10 h-10 items-center justify-center mb-6 border"
          >
            <ArrowLeft size={20} color="white" />
          </TouchableOpacity>

          <View className="mb-6">
            <View style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981' }} className="w-14 h-14 border rounded-2xl items-center justify-center mb-4 shadow-lg">
              <LogIn size={28} color="#34d399" />
            </View>
            <Text style={{ color: '#ffffff' }} className="text-3xl font-black">Iniciar Sesión</Text>
            <Text style={{ color: '#94a3b8' }} className="text-sm mt-1">Accede a tu panel financiero y tareas en KoreLife.</Text>
          </View>

          {/* Botón de Google OAuth */}
          <TouchableOpacity
            onPress={handleGoogleLogin}
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
                  Continuar con Google
                </Text>
              </>
            )}
          </TouchableOpacity>

          {/* Divisor con texto */}
          <View className="flex-row items-center mb-4">
            <View style={{ backgroundColor: '#1e293b' }} className="flex-1 h-px" />
            <Text style={{ color: '#64748b' }} className="text-xs font-bold uppercase mx-3">O con tu correo</Text>
            <View style={{ backgroundColor: '#1e293b' }} className="flex-1 h-px" />
          </View>

          {/* Correo */}
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
          <View className="mb-2">
            <Text style={{ color: '#cbd5e1' }} className="text-xs font-bold mb-1.5 uppercase">Contraseña</Text>
            <View style={{ backgroundColor: '#0f172a', borderColor: '#334155' }} className="flex-row items-center px-4 py-3.5 rounded-2xl border">
              <Lock size={18} color="#94a3b8" />
              <TextInput
                value={password}
                onChangeText={setPassword}
                placeholder="••••••••"
                placeholderTextColor="#64748b"
                secureTextEntry={!showPassword}
                style={{ color: '#ffffff' }}
                className="flex-1 ml-3 text-sm font-medium"
              />
              <TouchableOpacity onPress={() => setShowPassword(!showPassword)} className="p-1">
                {showPassword ? <EyeOff size={18} color="#94a3b8" /> : <Eye size={18} color="#94a3b8" />}
              </TouchableOpacity>
            </View>
          </View>

          {/* Olvidé mi contraseña */}
          <TouchableOpacity 
            onPress={() => router.push('/(auth)/forgot-password')} 
            className="self-end mb-5 py-1"
          >
            <Text style={{ color: '#34d399' }} className="text-xs font-bold">¿Olvidaste tu contraseña?</Text>
          </TouchableOpacity>

          {/* Botón Ingresar */}
          <TouchableOpacity
            onPress={handleLogin}
            disabled={loading}
            style={{ backgroundColor: '#10b981' }}
            className="py-4 rounded-2xl items-center shadow-xl active:bg-emerald-600 mb-3"
          >
            {loading ? (
              <ActivityIndicator color="#022c22" />
            ) : (
              <Text style={{ color: '#022c22' }} className="font-black text-base">Entrar a mi Cuenta</Text>
            )}
          </TouchableOpacity>

          {/* Botón Modo Rápido / Demo */}
          <TouchableOpacity
            onPress={handleQuickDemo}
            style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
            className="py-3.5 rounded-2xl items-center active:bg-slate-800 mb-5 shadow-sm"
          >
            <Text style={{ color: '#34d399' }} className="font-bold text-sm">Entrar en Modo Rápido / Demo</Text>
          </TouchableOpacity>
        </View>

        {/* Link a Registro */}
        <TouchableOpacity onPress={() => router.push('/(auth)/register')} className="items-center mb-4">
          <Text style={{ color: '#94a3b8' }} className="text-sm font-medium">
            ¿No tienes una cuenta? <Text style={{ color: '#34d399' }} className="font-black">Regístrate gratis</Text>
          </Text>
        </TouchableOpacity>
      </ScrollView>
    </SafeAreaView>
  );
}