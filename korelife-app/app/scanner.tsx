import React, { useState } from 'react';
import { View, Text, TouchableOpacity, Image, Alert, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import * as ImagePicker from 'expo-image-picker';
import { Feather } from '@expo/vector-icons';

export default function ScannerScreen() {
  const router = useRouter();
  const [imageUri, setImageUri] = useState<string | null>(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [parsedData, setParsedData] = useState<{ merchant: string; amount: string; date: string; category: string } | null>(null);

  const takePhoto = async () => {
    const permissionResult = await ImagePicker.requestCameraPermissionsAsync();
    if (!permissionResult.granted) {
      Alert.alert('Permisos requeridos', 'Se requiere acceso a la cámara para escanear recibos');
      return;
    }

    const result = await ImagePicker.launchCameraAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.8,
    });

    if (!result.canceled && result.assets[0]) {
      processReceipt(result.assets[0].uri);
    }
  };

  const pickImage = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.8,
    });

    if (!result.canceled && result.assets[0]) {
      processReceipt(result.assets[0].uri);
    }
  };

  const processReceipt = (uri: string) => {
    setImageUri(uri);
    setAnalyzing(true);
    setParsedData(null);

    // Simulación de OCR asistido por IA (Evernote OCR)
    setTimeout(() => {
      setAnalyzing(false);
      setParsedData({
        merchant: 'Supermercado Central',
        amount: '$48.50',
        date: new Date().toLocaleDateString('es-ES'),
        category: 'Alimentación',
      });
    }, 1800);
  };

  return (
    <SafeAreaView className="flex-1 bg-slate-950 p-6">
      <View className="flex-row items-center justify-between mb-6">
        <TouchableOpacity onPress={() => router.back()} className="p-2 bg-slate-900 rounded-full">
          <Feather name="arrow-left" size={24} color="#f8fafc" />
        </TouchableOpacity>
        <Text className="text-xl font-bold text-white">Escáner OCR de Recibos</Text>
        <View style={{ width: 40 }} />
      </View>

      <ScrollView showsVerticalScrollIndicator={false}>
        <View className="bg-slate-900 border border-dashed border-slate-700 rounded-2xl p-6 items-center justify-center min-h-[260px] mb-6">
          {imageUri ? (
            <Image source={{ uri: imageUri }} className="w-full h-64 rounded-xl resize-contain" />
          ) : (
            <View className="items-center">
              <View className="w-16 h-16 rounded-full bg-indigo-600/20 items-center justify-center mb-4">
                <Feather name="camera" size={32} color="#818cf8" />
              </View>
              <Text className="text-white font-semibold text-lg text-center">Digitaliza tus compras</Text>
              <Text className="text-slate-400 text-sm text-center mt-1 px-4">
                Toma una foto a una factura o recibo para extraer el monto y categorizarlo automáticamente.
              </Text>
            </View>
          )}
        </View>

        <View className="flex-row space-x-4 mb-6">
          <TouchableOpacity
            className="flex-1 bg-indigo-600 py-4 rounded-xl flex-row items-center justify-center space-x-2"
            onPress={takePhoto}
          >
            <Feather name="camera" size={20} color="#ffffff" />
            <Text className="text-white font-bold">Tomar Foto</Text>
          </TouchableOpacity>

          <TouchableOpacity
            className="flex-1 bg-slate-800 border border-slate-700 py-4 rounded-xl flex-row items-center justify-center space-x-2"
            onPress={pickImage}
          >
            <Feather name="image" size={20} color="#94a3b8" />
            <Text className="text-slate-200 font-bold">Galería</Text>
          </TouchableOpacity>
        </View>

        {analyzing && (
          <View className="bg-slate-900 border border-indigo-500/40 p-4 rounded-xl items-center mb-6">
            <Text className="text-indigo-400 font-medium">Analizando texto con OCR asistido por IA...</Text>
          </View>
        )}

        {parsedData && (
          <View className="bg-slate-900 border border-emerald-500/40 p-5 rounded-2xl space-y-3 mb-6">
            <View className="flex-row items-center justify-between pb-3 border-b border-slate-800">
              <Text className="text-slate-400 text-sm">Comercio detectado</Text>
              <Text className="text-white font-bold text-base">{parsedData.merchant}</Text>
            </View>
            <View className="flex-row items-center justify-between pb-3 border-b border-slate-800">
              <Text className="text-slate-400 text-sm">Monto total</Text>
              <Text className="text-emerald-400 font-extrabold text-xl">{parsedData.amount}</Text>
            </View>
            <View className="flex-row items-center justify-between pb-3 border-b border-slate-800">
              <Text className="text-slate-400 text-sm">Categoría asignada</Text>
              <Text className="text-indigo-400 font-semibold">{parsedData.category}</Text>
            </View>

            <TouchableOpacity
              className="bg-emerald-600 py-3.5 rounded-xl items-center mt-2"
              onPress={() => {
                Alert.alert('¡Registrado!', 'Transacción guardada exitosamente con el comprobante.');
                router.back();
              }}
            >
              <Text className="text-white font-bold text-base">Guardar en Finanzas</Text>
            </TouchableOpacity>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}
