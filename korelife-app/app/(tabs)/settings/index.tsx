import React, { useState } from 'react';
import { 
  View, 
  Text, 
  ScrollView, 
  TouchableOpacity, 
  Switch, 
  Alert, 
  TextInput, 
  Modal 
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { 
  User, 
  Wallet, 
  Bell, 
  Moon, 
  Sun,
  Shield, 
  Fingerprint, 
  Eye, 
  EyeOff, 
  Download, 
  Crown, 
  HelpCircle, 
  MessageSquare, 
  FileText, 
  LogOut, 
  Trash2, 
  ChevronRight, 
  Check, 
  Globe, 
  Sparkles,
  Smartphone,
  Lock,
  Edit3,
  X
} from 'lucide-react-native';
import { useAuthStore } from '../../../src/store/authStore';
import { useRatesStore } from '../../../src/store/ratesStore';
import { useSettingsStore } from '../../../src/store/settingsStore';
import { CurrencyCode, RateProvider } from '../../../src/types';
import BrandLogo from '../../../components/ui/BrandLogo';

export default function SettingsScreen() {
  const router = useRouter();
  const { user, signOut } = useAuthStore();
  const { rates, selectedProvider, setSelectedProvider } = useRatesStore();
  const settings = useSettingsStore();

  // Estados de modales de edición
  const [profileModalVisible, setProfileModalVisible] = useState(false);
  const [securityModalVisible, setSecurityModalVisible] = useState(false);
  
  // Formulario de edición de perfil
  const [editName, setEditName] = useState(user?.full_name || 'Carlos Pérez');
  const [editPhone, setEditPhone] = useState('+58 412 1234567');
  const [editBio, setEditBio] = useState('Inversionista & Emprendedor');

  // Formulario de cambio de contraseña
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');

  const currencies: { code: CurrencyCode; label: string; flag: string; symbol: string }[] = [
    { code: 'VES', label: 'Bolívares', flag: '🇻🇪', symbol: 'Bs.' },
    { code: 'USD', label: 'Dólares', flag: '🇺🇸', symbol: '$' },
    { code: 'EUR', label: 'Euros', flag: '🇪🇺', symbol: '€' }
  ];

  const rateProviders: { id: RateProvider; name: string; icon: string; desc: string }[] = [
    { id: 'bcv_usd', name: 'BCV Dólar Oficial', icon: '🏛️', desc: 'Tasa oficial publicada por el Banco Central' },
    { id: 'bcv_eur', name: 'BCV Euro Oficial', icon: '💶', desc: 'Cotización oficial en Euros BCV' },
    { id: 'binance', name: 'Binance P2P', icon: '🟡', desc: 'Promedio de compra/venta USDT en mercado P2P' },
    { id: 'airtm', name: 'Airtm', icon: '🔵', desc: 'Tasa de retiro AirUSD a Bolívares' }
  ];

  const accentColors = ['#10b981', '#6366f1', '#0ea5e9', '#f59e0b', '#ec4899'];

  const handleSaveProfile = () => {
    Alert.alert('Perfil Actualizado', 'Tus datos personales se han guardado con éxito.');
    setProfileModalVisible(false);
  };

  const handleSavePassword = () => {
    if (!currentPassword || !newPassword) {
      Alert.alert('Error', 'Por favor ingresa tu contraseña actual y la nueva.');
      return;
    }
    Alert.alert('Contraseña Modificada', 'Tu clave de acceso ha sido actualizada correctamente.');
    setCurrentPassword('');
    setNewPassword('');
    setSecurityModalVisible(false);
  };

  const handleExportData = (format: 'CSV' | 'JSON') => {
    Alert.alert(
      'Exportar Información',
      `Tus transacciones, cuentas y presupuestos han sido generados en formato ${format}. El archivo está listo para descargar.`
    );
  };

  const handleSignOut = () => {
    Alert.alert('Cerrar Sesión', '¿Estás seguro de que deseas salir de KoreLife?', [
      { text: 'Cancelar', style: 'cancel' },
      { 
        text: 'Cerrar Sesión', 
        style: 'destructive', 
        onPress: () => { 
          signOut(); 
          router.replace('/(auth)/login'); 
        } 
      }
    ]);
  };

  const handleDeleteAccount = () => {
    Alert.alert(
      '⚠️ Eliminar Cuenta',
      'Esta acción es irreversible y borrará permanentemente todas tus transacciones, cuentas, tareas y notas de KoreLife.',
      [
        { text: 'Cancelar', style: 'cancel' },
        { 
          text: 'Eliminar Definitivamente', 
          style: 'destructive', 
          onPress: () => {
            signOut();
            router.replace('/(auth)/login');
          }
        }
      ]
    );
  };

  return (
    <SafeAreaView className="flex-1 bg-slate-950 px-4">
      {/* Cabecera */}
      <View className="py-3 mb-2 flex-row justify-between items-center">
        <View>
          <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider">Centro de Control</Text>
          <Text className="text-white text-2xl font-black">Ajustes & Cuenta</Text>
        </View>
        <View className="bg-emerald-500/20 px-3 py-1 rounded-full border border-emerald-500/30 flex-row items-center">
          <Sparkles size={12} color="#10b981" />
          <Text className="text-emerald-400 text-xs font-bold ml-1">Pro Activo</Text>
        </View>
      </View>

      <ScrollView showsVerticalScrollIndicator={false} className="flex-1">
        {/* Card de Perfil */}
        <View className="bg-slate-900 p-4 rounded-3xl mb-5 border border-slate-800 shadow-md">
          <View className="flex-row items-center justify-between mb-3">
            <View className="flex-row items-center flex-1 mr-3">
              <View className="w-14 h-14 bg-gradient-to-tr from-emerald-600 to-indigo-600 rounded-2xl items-center justify-center mr-3.5 border border-white/20">
                <Text className="text-white font-black text-xl">
                  {(editName.charAt(0) || 'U').toUpperCase()}
                </Text>
              </View>
              <View className="flex-1">
                <Text className="text-white font-extrabold text-base" numberOfLines={1}>
                  {editName}
                </Text>
                <Text className="text-slate-400 text-xs">{user?.email || 'admin@korelife.com'}</Text>
                <Text className="text-emerald-400 text-[11px] font-semibold mt-0.5">{editPhone}</Text>
              </View>
            </View>

            <TouchableOpacity 
              onPress={() => setProfileModalVisible(true)}
              className="p-2.5 bg-slate-800 rounded-xl border border-slate-700 active:bg-slate-700"
            >
              <Edit3 size={16} color="#94a3b8" />
            </TouchableOpacity>
          </View>

          <View className="bg-slate-950/80 p-3 rounded-2xl border border-slate-800/80 flex-row justify-between items-center">
            <View>
              <Text className="text-slate-400 text-[10px] font-bold uppercase">Membresía</Text>
              <Text className="text-white font-bold text-xs">KoreLife Infinite Edition</Text>
            </View>
            <View className="bg-emerald-500/20 px-2.5 py-1 rounded-lg border border-emerald-500/30">
              <Text className="text-emerald-400 font-extrabold text-[10px]">SIN LÍMITES</Text>
            </View>
          </View>
        </View>

        {/* 1. SECCIÓN: MONEDAS Y TASAS DE CAMBIO */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2 px-1">
          Configuración Monetaria & Tasas
        </Text>
        <View className="bg-slate-900 rounded-3xl p-4 mb-5 border border-slate-800">
          {/* Selector de Moneda */}
          <Text className="text-white font-bold text-sm mb-2 flex-row items-center">
            Moneda Base Principal
          </Text>
          <View className="flex-row space-x-2 mb-4">
            {currencies.map((c) => {
              const isSel = settings.preferredCurrency === c.code;
              return (
                <TouchableOpacity
                  key={c.code}
                  onPress={() => {
                    settings.setPreferredCurrency(c.code);
                    Alert.alert('Moneda Principal', `Moneda base cambiada a ${c.label} (${c.code}).`);
                  }}
                  className={`flex-1 p-3 rounded-2xl border items-center ${
                    isSel ? 'bg-emerald-950/60 border-emerald-500' : 'bg-slate-800 border-slate-700'
                  }`}
                >
                  <Text className="text-xl mb-1">{c.flag}</Text>
                  <Text className={`font-extrabold text-xs ${isSel ? 'text-emerald-400' : 'text-slate-300'}`}>
                    {c.code}
                  </Text>
                  <Text className="text-slate-400 text-[10px]">{c.symbol}</Text>
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Selector de Tasa por Defecto */}
          <Text className="text-white font-bold text-sm mb-2">
            Tasa de Referencia Activa para Conversiones
          </Text>
          {rateProviders.map((rp) => {
            const isSel = selectedProvider === rp.id;
            const currentRate = rates[rp.id]?.rate || 0;
            return (
              <TouchableOpacity
                key={rp.id}
                onPress={() => setSelectedProvider(rp.id)}
                style={{
                  backgroundColor: isSel ? '#1e1b4b' : '#1e293b',
                  borderColor: isSel ? '#6366f1' : '#334155',
                  borderWidth: isSel ? 1.5 : 1
                }}
                className="p-3.5 rounded-2xl mb-2.5 flex-row items-center justify-between"
              >
                <View className="flex-row items-center flex-1 mr-2">
                  <BrandLogo provider={rp.id} size={28} />
                  <View className="flex-1 ml-3">
                    <Text className={`font-bold text-xs ${isSel ? 'text-indigo-200' : 'text-white'}`}>
                      {rp.name}
                    </Text>
                    <Text className="text-slate-400 text-[10px]" numberOfLines={1}>{rp.desc}</Text>
                  </View>
                </View>

                <View className="items-end">
                  <Text className="text-emerald-400 font-black text-sm">
                    Bs. {currentRate.toFixed(2)}
                  </Text>
                  {isSel && (
                    <Text className="text-indigo-400 text-[9px] font-bold uppercase mt-0.5">Activa</Text>
                  )}
                </View>
              </TouchableOpacity>
            );
          })}

          {/* Switch de Auto-Actualización */}
          <View className="flex-row justify-between items-center pt-3 mt-1 border-t border-slate-800">
            <View className="flex-1 mr-3">
              <Text className="text-white text-xs font-semibold">Actualizar tasas al abrir la app</Text>
              <Text className="text-slate-400 text-[10px]">Descarga cotizaciones frescas de BCV y Binance</Text>
            </View>
            <Switch
              value={settings.autoUpdateRates}
              onValueChange={settings.toggleAutoUpdateRates}
              thumbColor={settings.autoUpdateRates ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>
        </View>

        {/* 2. SECCIÓN: SEGURIDAD Y PRIVACIDAD */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2 px-1">
          Seguridad & Privacidad
        </Text>
        <View className="bg-slate-900 rounded-3xl p-4 mb-5 border border-slate-800">
          {/* Biometría */}
          <View className="flex-row justify-between items-center mb-4 pb-3 border-b border-slate-800">
            <View className="flex-row items-center flex-1 mr-3">
              <View className="p-2 rounded-xl bg-slate-800 mr-3">
                <Fingerprint size={18} color="#10b981" />
              </View>
              <View className="flex-1">
                <Text className="text-white text-xs font-bold">Bloqueo con FaceID / Huella</Text>
                <Text className="text-slate-400 text-[10px]">Solicitar biometría al abrir KoreLife</Text>
              </View>
            </View>
            <Switch
              value={settings.biometricsEnabled}
              onValueChange={settings.toggleBiometrics}
              thumbColor={settings.biometricsEnabled ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>

          {/* Ocultar Saldos */}
          <View className="flex-row justify-between items-center mb-4 pb-3 border-b border-slate-800">
            <View className="flex-row items-center flex-1 mr-3">
              <View className="p-2 rounded-xl bg-slate-800 mr-3">
                {settings.hideBalances ? <EyeOff size={18} color="#f59e0b" /> : <Eye size={18} color="#f59e0b" />}
              </View>
              <View className="flex-1">
                <Text className="text-white text-xs font-bold">Modo Privacidad en Pantalla</Text>
                <Text className="text-slate-400 text-[10px]">Ocultar montos en lugares públicos</Text>
              </View>
            </View>
            <Switch
              value={settings.hideBalances}
              onValueChange={settings.toggleHideBalances}
              thumbColor={settings.hideBalances ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>

          {/* Cambiar Contraseña */}
          <TouchableOpacity 
            onPress={() => setSecurityModalVisible(true)}
            className="flex-row justify-between items-center pt-1"
          >
            <View className="flex-row items-center">
              <View className="p-2 rounded-xl bg-slate-800 mr-3">
                <Lock size={18} color="#818cf8" />
              </View>
              <View>
                <Text className="text-white text-xs font-bold">Cambiar Contraseña</Text>
                <Text className="text-slate-400 text-[10px]">Actualizar tu clave de seguridad</Text>
              </View>
            </View>
            <ChevronRight size={16} color="#64748b" />
          </TouchableOpacity>
        </View>

        {/* 3. SECCIÓN: NOTIFICACIONES */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2 px-1">
          Notificaciones & Recordatorios
        </Text>
        <View className="bg-slate-900 rounded-3xl p-4 mb-5 border border-slate-800">
          <View className="flex-row justify-between items-center mb-3 pb-3 border-b border-slate-800">
            <View className="flex-1 mr-3">
              <Text className="text-white text-xs font-semibold">Alertas de Variación de Tasa</Text>
              <Text className="text-slate-400 text-[10px]">Aviso cuando el dólar BCV o Binance cambie</Text>
            </View>
            <Switch
              value={settings.rateAlerts}
              onValueChange={settings.toggleRateAlerts}
              thumbColor={settings.rateAlerts ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>

          <View className="flex-row justify-between items-center mb-3 pb-3 border-b border-slate-800">
            <View className="flex-1 mr-3">
              <Text className="text-white text-xs font-semibold">Recordatorio Diario de Gastos</Text>
              <Text className="text-slate-400 text-[10px]">Notificación nocturna para registrar movimientos</Text>
            </View>
            <Switch
              value={settings.dailyReminder}
              onValueChange={settings.toggleDailyReminder}
              thumbColor={settings.dailyReminder ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>

          <View className="flex-row justify-between items-center">
            <View className="flex-1 mr-3">
              <Text className="text-white text-xs font-semibold">Vencimiento de Suscripciones</Text>
              <Text className="text-slate-400 text-[10px]">Avisar 2 días antes de cada cobro recurrente</Text>
            </View>
            <Switch
              value={settings.subscriptionAlerts}
              onValueChange={settings.toggleSubscriptionAlerts}
              thumbColor={settings.subscriptionAlerts ? '#10b981' : '#64748b'}
              trackColor={{ false: '#334155', true: '#064e3b' }}
            />
          </View>
        </View>

        {/* 4. SECCIÓN: EXPORTACIÓN & DATOS */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2 px-1">
          Exportar Información Financiera
        </Text>
        <View className="bg-slate-900 rounded-3xl p-4 mb-5 border border-slate-800">
          <Text className="text-slate-400 text-xs mb-3">
            Descarga un respaldo completo de tus transacciones, cuentas y balances en formato compatible con Excel o JSON.
          </Text>
          <View className="flex-row space-x-2">
            <TouchableOpacity 
              onPress={() => handleExportData('CSV')}
              className="flex-1 bg-slate-800 py-3 rounded-2xl border border-slate-700 flex-row items-center justify-center"
            >
              <Download size={14} color="#10b981" />
              <Text className="text-white font-bold text-xs ml-1.5">Exportar CSV</Text>
            </TouchableOpacity>

            <TouchableOpacity 
              onPress={() => handleExportData('JSON')}
              className="flex-1 bg-slate-800 py-3 rounded-2xl border border-slate-700 flex-row items-center justify-center"
            >
              <Download size={14} color="#38bdf8" />
              <Text className="text-white font-bold text-xs ml-1.5">Exportar JSON</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* 5. SECCIÓN: SOPORTE Y LEGAL */}
        <Text className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2 px-1">
          Centro de Asistencia & Legal
        </Text>
        <View className="bg-slate-900 rounded-3xl p-4 mb-5 border border-slate-800">
          <TouchableOpacity 
            onPress={() => router.push('/social/support')}
            className="flex-row justify-between items-center py-2.5 border-b border-slate-800"
          >
            <View className="flex-row items-center">
              <HelpCircle size={16} color="#10b981" />
              <Text className="text-white font-bold text-xs ml-2.5">Preguntas Frecuentes & FAQ</Text>
            </View>
            <ChevronRight size={14} color="#64748b" />
          </TouchableOpacity>

          <TouchableOpacity 
            onPress={() => Alert.alert('Soporte KoreLife', 'Puedes escribirnos directamente a soporte@korelife.com o abrir un ticket.')}
            className="flex-row justify-between items-center py-2.5 border-b border-slate-800"
          >
            <View className="flex-row items-center">
              <MessageSquare size={16} color="#38bdf8" />
              <Text className="text-white font-bold text-xs ml-2.5">Contactar a Soporte 24/7</Text>
            </View>
            <ChevronRight size={14} color="#64748b" />
          </TouchableOpacity>

          <TouchableOpacity 
            onPress={() => Alert.alert('Términos y Privacidad', 'KoreLife protege tus datos financieros con cifrado AES-256 de nivel bancario.')}
            className="flex-row justify-between items-center py-2.5"
          >
            <View className="flex-row items-center">
              <FileText size={16} color="#818cf8" />
              <Text className="text-white font-bold text-xs ml-2.5">Política de Privacidad y Términos</Text>
            </View>
            <ChevronRight size={14} color="#64748b" />
          </TouchableOpacity>
        </View>

        {/* 6. SECCIÓN: ACCIONES DE SESIÓN Y CUENTA */}
        <View className="space-y-3 mb-10">
          <TouchableOpacity
            onPress={handleSignOut}
            className="bg-slate-900 p-4 rounded-2xl border border-slate-800 flex-row items-center justify-center active:bg-slate-800 mb-2"
          >
            <LogOut size={16} color="#f43f5e" />
            <Text className="text-rose-400 font-bold ml-2 text-sm">Cerrar Sesión</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={handleDeleteAccount}
            className="p-3 items-center"
          >
            <Text className="text-slate-400 text-xs font-semibold">Eliminar mi cuenta y datos</Text>
          </TouchableOpacity>

          <Text className="text-slate-400 text-[11px] text-center pt-2">
            KoreLife App v2.4.0 (Build SDK 54) • Hecho para Venezuela 🇻🇪
          </Text>
        </View>
        <View className="h-10" />
      </ScrollView>

      {/* MODAL: EDITAR PERFIL */}
      <Modal visible={profileModalVisible} animationType="slide" transparent onRequestClose={() => setProfileModalVisible(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155', maxHeight: '88%' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text className="text-white text-lg font-black">Editar Perfil</Text>
              <TouchableOpacity onPress={() => setProfileModalVisible(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Nombre Completo</Text>
            <TextInput
              value={editName}
              onChangeText={setEditName}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
              className="text-white p-3.5 rounded-xl border text-sm mb-4"
            />

            <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Teléfono de Contacto</Text>
            <TextInput
              value={editPhone}
              onChangeText={setEditPhone}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
              className="text-white p-3.5 rounded-xl border text-sm mb-4"
            />

            <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Biografía / Ocupación</Text>
            <TextInput
              value={editBio}
              onChangeText={setEditBio}
              style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
              className="text-white p-3.5 rounded-xl border text-sm mb-6"
            />

            <TouchableOpacity 
              onPress={handleSaveProfile}
              style={{ backgroundColor: '#10b981' }}
              className="py-4 rounded-xl items-center shadow-lg active:bg-emerald-600 mb-4"
            >
              <Text className="text-slate-950 font-black text-sm">Guardar Cambios ✅</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* MODAL: CAMBIAR CONTRASEÑA */}
      <Modal visible={securityModalVisible} animationType="slide" transparent onRequestClose={() => setSecurityModalVisible(false)}>
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(2, 6, 23, 0.85)' }}>
          <View style={{ backgroundColor: '#0f172a', borderTopLeftRadius: 28, borderTopRightRadius: 28, borderTopWidth: 1, borderColor: '#334155', maxHeight: '88%' }} className="p-6">
            <View className="flex-row justify-between items-center pb-3 border-b border-slate-800 mb-4">
              <Text className="text-white text-lg font-black">Cambiar Contraseña</Text>
              <TouchableOpacity onPress={() => setSecurityModalVisible(false)} style={{ backgroundColor: '#1e293b' }} className="p-2 rounded-full border border-slate-700">
                <X size={18} color="#94a3b8" />
              </TouchableOpacity>
            </View>

            <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Contraseña Actual</Text>
            <TextInput
              value={currentPassword}
              onChangeText={setCurrentPassword}
              placeholder="••••••••"
              placeholderTextColor="#64748b"
              secureTextEntry
              style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
              className="text-white p-3.5 rounded-xl border text-sm mb-4"
            />

            <Text className="text-slate-300 text-xs font-bold mb-1.5 uppercase">Nueva Contraseña</Text>
            <TextInput
              value={newPassword}
              onChangeText={setNewPassword}
              placeholder="Mínimo 6 caracteres"
              placeholderTextColor="#64748b"
              secureTextEntry
              style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
              className="text-white p-3.5 rounded-xl border text-sm mb-6"
            />

            <TouchableOpacity 
              onPress={handleSavePassword}
              style={{ backgroundColor: '#6366f1' }}
              className="py-4 rounded-xl items-center shadow-lg active:bg-indigo-600 mb-4"
            >
              <Text className="text-white font-black text-sm">Actualizar Clave 🔒</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}