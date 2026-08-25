import { Stack } from 'expo-router';

export default function SocialLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
        contentStyle: { backgroundColor: '#020617' },
      }}
    >
      <Stack.Screen name="index" />
      <Stack.Screen name="groups" />
      <Stack.Screen name="messages" />
      <Stack.Screen name="support" />
    </Stack>
  );
}
