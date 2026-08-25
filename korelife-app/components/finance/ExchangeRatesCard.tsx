import React, { memo, useCallback } from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { RefreshCw, TrendingUp, TrendingDown, Check } from 'lucide-react-native';
import { useRatesStore } from '../../src/store/ratesStore';
import { RateProvider } from '../../src/types';
import BrandLogo from '../ui/BrandLogo';

function ExchangeRatesCardComponent() {
  const rates = useRatesStore((s) => s.rates);
  const selectedProvider = useRatesStore((s) => s.selectedProvider);
  const setSelectedProvider = useRatesStore((s) => s.setSelectedProvider);
  const fetchRates = useRatesStore((s) => s.fetchRates);
  const isLoading = useRatesStore((s) => s.isLoading);

  const providers: RateProvider[] = ['bcv_usd', 'bcv_eur', 'binance', 'airtm'];

  const handleRefresh = useCallback(() => {
    fetchRates();
  }, [fetchRates]);

  return (
    <View 
      style={{ backgroundColor: '#0f172a', borderColor: '#1e293b' }}
      className="rounded-3xl p-4 mb-5 border shadow-xl"
    >
      {/* Header */}
      <View className="flex-row justify-between items-center mb-3">
        <View className="flex-row items-center">
          <Text style={{ color: '#ffffff' }} className="font-black text-base mr-2">
            Tasas en Tiempo Real
          </Text>
          <View style={{ backgroundColor: 'rgba(16, 185, 129, 0.15)', borderColor: '#10b981' }} className="px-2.5 py-0.5 rounded-full border flex-row items-center">
            <View style={{ width: 6, height: 6, borderRadius: 3, backgroundColor: '#34d399', marginRight: 5 }} />
            <Text style={{ color: '#34d399' }} className="text-[10px] font-black tracking-wider">
              EN VIVO
            </Text>
          </View>
        </View>

        <TouchableOpacity 
          onPress={handleRefresh} 
          disabled={isLoading}
          style={{ backgroundColor: '#1e293b', borderColor: '#334155' }}
          className="flex-row items-center px-3 py-1.5 rounded-xl border active:opacity-80"
        >
          {isLoading ? (
            <ActivityIndicator size="small" color="#10b981" />
          ) : (
            <>
              <RefreshCw size={12} color="#94a3b8" />
              <Text style={{ color: '#cbd5e1' }} className="text-xs ml-1.5 font-bold">
                Actualizar
              </Text>
            </>
          )}
        </TouchableOpacity>
      </View>

      {/* Grid de Tasas con Logos Oficiales */}
      <ScrollView horizontal showsHorizontalScrollIndicator={false} className="flex-row -mx-1">
        {providers.map((pKey) => {
          const item = rates[pKey];
          const isSelected = selectedProvider === pKey;

          return (
            <TouchableOpacity
              key={pKey}
              onPress={() => setSelectedProvider(pKey)}
              style={{
                backgroundColor: isSelected ? '#06281e' : '#1e293b',
                borderColor: isSelected ? '#10b981' : '#334155',
                borderWidth: isSelected ? 1.5 : 1,
                minWidth: 155,
              }}
              className="mr-3 p-3.5 rounded-2xl shadow-sm active:opacity-90"
            >
              <View className="flex-row items-center justify-between mb-2">
                <View className="flex-row items-center flex-1 mr-1">
                  <BrandLogo provider={pKey} size={28} />
                  <View className="ml-2 flex-1">
                    <Text style={{ color: '#ffffff' }} className="text-xs font-black" numberOfLines={1}>
                      {item.name}
                    </Text>
                    <Text style={{ color: '#94a3b8' }} className="text-[10px] font-medium">
                      {item.symbol}
                    </Text>
                  </View>
                </View>
                {isSelected && (
                  <View style={{ backgroundColor: '#10b981', width: 20, height: 20, borderRadius: 10 }} className="items-center justify-center shadow-sm">
                    <Check size={12} color="#022c22" strokeWidth={3} />
                  </View>
                )}
              </View>

              <Text style={{ color: '#ffffff' }} className="font-black text-xl tracking-tight mt-1">
                Bs. {item.rate.toLocaleString('es-VE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </Text>

              <View style={{ borderTopColor: '#334155' }} className="flex-row items-center justify-between mt-2 pt-2 border-t">
                <Text style={{ color: '#64748b' }} className="text-[10px] font-medium">
                  {item.lastUpdated || 'Hoy'}
                </Text>
                {item.change24h !== undefined && (
                  <View className="flex-row items-center">
                    {item.change24h >= 0 ? (
                      <TrendingUp size={11} color="#34d399" />
                    ) : (
                      <TrendingDown size={11} color="#f87171" />
                    )}
                    <Text 
                      style={{ color: item.change24h >= 0 ? '#34d399' : '#f87171' }} 
                      className="text-[10px] ml-0.5 font-bold"
                    >
                      {item.change24h >= 0 ? '+' : ''}{item.change24h}%
                    </Text>
                  </View>
                )}
              </View>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
      
      <Text style={{ color: '#64748b' }} className="text-[11px] mt-3 text-center font-medium">
        💡 Toca una tasa para fijarla como referencia en tus cálculos
      </Text>
    </View>
  );
}

export default memo(ExchangeRatesCardComponent);