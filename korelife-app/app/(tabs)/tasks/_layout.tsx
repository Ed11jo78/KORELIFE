import { Stack } from 'expo-router';

export default function TasksLayout() {
  return (
    <Stack
      screenOptions={{
        headerShown: false,
      }}
    >
      <Stack.Screen name="index" />
      <Stack.Screen name="projects" />
      <Stack.Screen name="kanban" />
      <Stack.Screen name="habits" />
      <Stack.Screen name="notes" />
    </Stack>
  );
}
