import { Stack } from 'expo-router';

export default function FinanceLayout() {
  return (
    <Stack screenOptions={{ headerShown: false }}>
      <Stack.Screen name="index" options={{ title: 'Finanzas' }} />
      <Stack.Screen name="transactions" options={{ title: 'Transacciones', presentation: 'modal' }} />
      <Stack.Screen name="budget" options={{ title: 'Presupuesto' }} />
      <Stack.Screen name="goals" options={{ title: 'Metas' }} />
      <Stack.Screen name="subscriptions" options={{ title: 'Suscripciones' }} />
      <Stack.Screen name="wealth" options={{ title: 'Patrimonio' }} />
      <Stack.Screen name="analytics" options={{ title: 'Analíticas' }} />
    </Stack>
  );
}
