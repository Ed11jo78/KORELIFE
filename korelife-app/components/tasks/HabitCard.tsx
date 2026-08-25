import { Feather } from '@expo/vector-icons';
import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { Check, Plus, Clock, Play, Pause, RotateCcw, Zap } from 'lucide-react-native';

export default function HabitCard({ title, icon, progress, color }: { title: string, icon: string, progress: number, color: string }) {
  const isCompleted = progress === 100;
  
  return (
    <TouchableOpacity className="w-[48%] bg-white p-4 rounded-xl shadow-sm border border-slate-100 mb-4 items-center">
      <View className={`w-14 h-14 rounded-full ${isCompleted ? color : 'bg-slate-100'} items-center justify-center mb-3 relative`}>
        <Feather name={icon as any} size={24} color={isCompleted ? 'white' : '#64748b'} />
        {/* Simple progress ring representation via border for non-completed */}
        {!isCompleted && (
          <View className={`absolute top-0 left-0 right-0 bottom-0 rounded-full border-4 ${color.replace('bg-', 'border-')} opacity-30`} />
        )}
      </View>
      <Text className="font-semibold text-slate-800 text-center">{title}</Text>
      <Text className="text-xs text-slate-500 mt-1">{progress}%</Text>
    </TouchableOpacity>
  );
}


