import React, { memo } from 'react';
import { View, Text } from 'react-native';
import { Wallet, CreditCard, Banknote, TrendingUp } from 'lucide-react-native';
import { useRatesStore } from '../../src/store/ratesStore';
import { CurrencyCode } from '../../src/types';
import BrandLogo from '../ui/BrandLogo';

interface Props {
  name: string;
  balance: number;
  type: 'bank' | 'credit' | 'cash' | 'investment';
  currency: CurrencyCode;
}

function AccountCardComponent({ name, balance, type, currency }: Props) {
  const convert = useRatesStore((s) => s.convert);
  const formatCurrency = useRatesStore((s) => s.formatCurrency);

  const getIcon = () => {
    if (name.toLowerCase().includes('binance')) {
      return <BrandLogo provider="binance" size={24} />;
    }
    if (name.toLowerCase().includes('airtm')) {
      return <BrandLogo provider="airtm" size={24} />;
    }
    switch (type) {
      case 'credit': return <CreditCard size={20} color="#c084fc" />;
      case 'cash': return <Banknote size={20} color="#4ade80" />;
      case 'investment': return <TrendingUp size={20} color="#38bdf8" />;
      default: return <Wallet size={20} color="#818cf8" />;
    }
  };

  const secondaryCurrency: CurrencyCode = currency === 'VES' ? 'USD' : 'VES';
  const convertedBalance = convert(balance, currency, secondaryCurrency);

  return (
    <View 
      style={{ backgroundColor: '#0f172a', borderColor: '#334155', borderWidth: 1 }}
      className="p-4 rounded-3xl w-56 shadow-lg mr-3"
    >
      <View className="flex-row items-center justify-between mb-3">
        <View style={{ backgroundColor: '#1e293b', borderColor: '#475569' }} className="p-2.5 rounded-2xl border">
          {getIcon()}
        </View>
        <View style={{ backgroundColor: '#1e293b', borderColor: '#334155' }} className="px-2.5 py-1 rounded-xl border">
          <Text style={{ color: '#e2e8f0' }} className="text-xs font-black tracking-wider">{currency}</Text>
        </View>
      </View>

      <Text style={{ color: '#94a3b8' }} className="text-xs font-bold mb-1" numberOfLines={1}>
        {name}
      </Text>

      <Text style={{ color: '#ffffff' }} className="font-black text-xl tracking-tight">
        {formatCurrency(balance, currency)}
      </Text>

      <Text style={{ color: '#34d399' }} className="text-xs font-bold mt-1">
        ≈ {formatCurrency(convertedBalance, secondaryCurrency)}
      </Text>
    </View>
  );
}

export default memo(AccountCardComponent);