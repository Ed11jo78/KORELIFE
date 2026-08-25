import { Feather } from '@expo/vector-icons';
import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { Check, Plus, Clock, Play, Pause, RotateCcw, Zap } from 'lucide-react-native';

export default function TaskItem({ task }: { task: any }) {
  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'high': return 'bg-red-500';
      case 'medium': return 'bg-yellow-500';
      case 'low': return 'bg-blue-500';
      default: return 'bg-slate-400';
    }
  };

  return (
    <View className="flex-row items-center bg-white p-4 rounded-xl mb-3 shadow-sm border border-slate-100">
      <TouchableOpacity className={`w-6 h-6 rounded border-2 items-center justify-center mr-3 ${task.completed ? 'border-indigo-500 bg-indigo-500' : 'border-slate-300'}`}>
        {task.completed && <Feather name="check" size={16} color="white" />}
      </TouchableOpacity>
      
      <View className="flex-1">
        <Text className={`text-base font-medium ${task.completed ? 'text-slate-400 line-through' : 'text-slate-800'}`}>
          {task.title}
        </Text>
        {(task.date || task.project) && (
          <View className="flex-row mt-1 items-center">
            {task.date && (
              <Text className="text-xs text-slate-500 mr-3"><Feather name="calendar" size={10} /> {task.date}</Text>
            )}
            {task.project && (
              <Text className="text-xs text-indigo-500"><Feather name="folder" size={10} /> {task.project}</Text>
            )}
          </View>
        )}
      </View>

      <View className={`w-3 h-3 rounded-full ${getPriorityColor(task.priority)} ml-2`} />
    </View>
  );
}


