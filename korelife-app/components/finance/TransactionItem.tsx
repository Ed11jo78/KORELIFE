import React, { memo } from 'react';
import { View, Text } from 'react-native';
import { ArrowDownLeft, ArrowUpRight } from 'lucide-react-native';
import { useRatesStore } from '../../src/store/ratesStore';
import { Transaction, CurrencyCode } from '../../src/types';

interface Props {
  transaction: Transaction;
}

function TransactionItemComponent({ transaction }: Props) {
  const convert = useRatesStore((s) => s.convert);
  const formatCurrency = useRatesStore((s) => s.formatCurrency);
  const isIncome = transaction.type === 'income';

  const secondaryCurrency: CurrencyCode = transaction.currency === 'VES' ? 'USD' : 'VES';
  const convertedAmount = convert(transaction.amount, transaction.currency, secondaryCurrency);

  return (
    <View 
      style={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderWidth: 1 }}
      className="p-4 rounded-2xl mb-2.5 flex-row items-center justify-between shadow-sm"
    >
      <View className="flex-row items-center flex-1 mr-3">
        <View 
          style={{ 
            backgroundColor: isIncome ? 'rgba(16, 185, 129, 0.15)' : 'rgba(244, 63, 94, 0.15)',
            borderColor: isIncome ? '#10b981' : '#f43f5e',
            width: 44,
            height: 44,
            borderRadius: 16
          }}
          className="items-center justify-center mr-3.5 border"
        >
          {isIncome ? (
            <ArrowDownLeft size={22} color="#34d399" />
          ) : (
            <ArrowUpRight size={22} color="#fb7185" />
          )}
        </View>

        <View className="flex-1">
          <Text style={{ color: '#ffffff' }} className="font-bold text-sm" numberOfLines={1}>
            {transaction.description || transaction.category}
          </Text>
          <Text style={{ color: '#94a3b8' }} className="text-xs mt-0.5 font-medium">
            {transaction.category} • {transaction.date}
          </Text>
        </View>
      </View>

      <View className="items-end">
        <Text 
          style={{ color: isIncome ? '#34d399' : '#ffffff' }} 
          className="font-black text-base"
        >
          {isIncome ? '+' : '-'}{formatCurrency(transaction.amount, transaction.currency)}
        </Text>
        <Text style={{ color: '#64748b' }} className="text-[11px] mt-0.5 font-semibold">
          ≈ {formatCurrency(convertedAmount, secondaryCurrency)}
        </Text>
      </View>
    </View>
  );
}

export default memo(TransactionItemComponent);