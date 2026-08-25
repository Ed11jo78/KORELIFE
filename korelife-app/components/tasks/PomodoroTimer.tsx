import { Feather } from '@expo/vector-icons';
import React, { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { Check, Plus, Clock, Play, Pause, RotateCcw, Zap } from 'lucide-react-native';

export default function PomodoroTimer() {
  const [timeLeft, setTimeLeft] = useState(25 * 60);
  const [isActive, setIsActive] = useState(false);
  const [mode, setMode] = useState<'work' | 'break'>('work');

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (isActive && timeLeft > 0) {
      interval = setInterval(() => setTimeLeft((prev) => prev - 1), 1000);
    } else if (timeLeft === 0) {
      setIsActive(false);
      // toggle mode
      if (mode === 'work') {
        setMode('break');
        setTimeLeft(5 * 60);
      } else {
        setMode('work');
        setTimeLeft(25 * 60);
      }
    }
    return () => clearInterval(interval);
  }, [isActive, timeLeft, mode]);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <View className="bg-white p-6 rounded-2xl shadow-sm items-center border border-slate-100">
      <View className="flex-row mb-4">
        <TouchableOpacity onPress={() => { setMode('work'); setTimeLeft(25 * 60); setIsActive(false); }} className={`px-4 py-1 rounded-full mr-2 ${mode === 'work' ? 'bg-indigo-100' : ''}`}>
          <Text className={`font-semibold ${mode === 'work' ? 'text-indigo-700' : 'text-slate-500'}`}>Trabajo</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { setMode('break'); setTimeLeft(5 * 60); setIsActive(false); }} className={`px-4 py-1 rounded-full ${mode === 'break' ? 'bg-indigo-100' : ''}`}>
          <Text className={`font-semibold ${mode === 'break' ? 'text-indigo-700' : 'text-slate-500'}`}>Descanso</Text>
        </TouchableOpacity>
      </View>

      <Text className="text-6xl font-black text-slate-800 mb-6 font-mono">{formatTime(timeLeft)}</Text>

      <View className="flex-row items-center space-x-6">
        <TouchableOpacity onPress={() => setIsActive(!isActive)} className="bg-indigo-600 w-16 h-16 rounded-full items-center justify-center shadow-md">
          <Feather name={isActive ? "pause" : "play"} size={32} color="white" style={!isActive ? { marginLeft: 4 } : {}} />
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { setIsActive(false); setTimeLeft(mode === 'work' ? 25 * 60 : 5 * 60); }} className="w-12 h-12 rounded-full bg-slate-100 items-center justify-center">
          <Feather name="rotate-ccw" size={24} color="#64748b" />
        </TouchableOpacity>
      </View>
    </View>
  );
}


