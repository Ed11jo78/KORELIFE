import { create } from 'zustand';
import { CurrencyCode, RateProvider } from '../types';

interface SettingsState {
  theme: 'dark' | 'light' | 'system';
  accentColor: string;
  preferredCurrency: CurrencyCode;
  preferredRateProvider: RateProvider;
  autoUpdateRates: boolean;
  hideBalances: boolean;
  biometricsEnabled: boolean;
  rateAlerts: boolean;
  dailyReminder: boolean;
  subscriptionAlerts: boolean;
  setTheme: (theme: 'dark' | 'light' | 'system') => void;
  setAccentColor: (color: string) => void;
  setPreferredCurrency: (currency: CurrencyCode) => void;
  setPreferredRateProvider: (provider: RateProvider) => void;
  toggleAutoUpdateRates: () => void;
  toggleHideBalances: () => void;
  toggleBiometrics: () => void;
  toggleRateAlerts: () => void;
  toggleDailyReminder: () => void;
  toggleSubscriptionAlerts: () => void;
}

export const useSettingsStore = create<SettingsState>((set) => ({
  theme: 'dark',
  accentColor: '#10b981',
  preferredCurrency: 'VES',
  preferredRateProvider: 'bcv_usd',
  autoUpdateRates: true,
  hideBalances: false,
  biometricsEnabled: true,
  rateAlerts: true,
  dailyReminder: true,
  subscriptionAlerts: true,

  setTheme: (theme) => set({ theme }),
  setAccentColor: (accentColor) => set({ accentColor }),
  setPreferredCurrency: (preferredCurrency) => set({ preferredCurrency }),
  setPreferredRateProvider: (preferredRateProvider) => set({ preferredRateProvider }),
  toggleAutoUpdateRates: () => set((s) => ({ autoUpdateRates: !s.autoUpdateRates })),
  toggleHideBalances: () => set((s) => ({ hideBalances: !s.hideBalances })),
  toggleBiometrics: () => set((s) => ({ biometricsEnabled: !s.biometricsEnabled })),
  toggleRateAlerts: () => set((s) => ({ rateAlerts: !s.rateAlerts })),
  toggleDailyReminder: () => set((s) => ({ dailyReminder: !s.dailyReminder })),
  toggleSubscriptionAlerts: () => set((s) => ({ subscriptionAlerts: !s.subscriptionAlerts }))
}));