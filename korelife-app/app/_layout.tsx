import React, { useEffect } from 'react';
import { Stack } from 'expo-router';
import { View, Platform } from 'react-native';
import { supabase } from '../src/lib/supabase';
import { useAuthStore } from '../src/store/authStore';
import { useRatesStore } from '../src/store/ratesStore';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { PaperProvider, MD3DarkTheme } from 'react-native-paper';

const customDarkTheme = {
  ...MD3DarkTheme,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#10b981',
    background: '#020617',
    surface: '#0f172a',
    onSurface: '#ffffff',
  },
};

export default function RootLayout() {
  const { setSession } = useAuthStore();
  const { fetchRates } = useRatesStore();

  useEffect(() => {
    supabase.auth.getSession().then(({ data: { session } }) => {
      if (session) {
        setSession(session);
      }
    }).catch((err) => {
      console.warn('Error fetching initial session:', err);
    });

    const { data: authListener } = supabase.auth.onAuthStateChange((_event, session) => {
      setSession(session);
    });

    fetchRates().catch(() => {});

    return () => {
      authListener?.subscription?.unsubscribe();
    };
  }, []);

  return (
    <GestureHandlerRootView style={{ flex: 1, backgroundColor: '#020617' }}>
      <SafeAreaProvider style={{ flex: 1, backgroundColor: '#020617' }}>
        <PaperProvider theme={customDarkTheme}>
          <View style={{ flex: 1, backgroundColor: '#020617' }}>
            <Stack 
              screenOptions={{ 
                headerShown: false, 
                animation: 'fade',
                contentStyle: { backgroundColor: '#020617' } 
              }}
            >
              <Stack.Screen name="index" />
              <Stack.Screen name="(auth)" />
              <Stack.Screen name="(tabs)" />
            </Stack>
          </View>
        </PaperProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}