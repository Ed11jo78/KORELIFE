import { create } from 'zustand';
import { supabase } from '../lib/supabase';
import { Session, User } from '@supabase/supabase-js';

type AuthState = {
  user: User | null;
  session: Session | null;
  loading: boolean;
  setSession: (session: Session | null) => void;
  signOut: () => Promise<void>;
};

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  session: null,
  loading: false, // Default to false so web and mobile never freeze on a blank screen
  setSession: (session) => set({ session, user: session?.user || null, loading: false }),
  signOut: async () => {
    try {
      await supabase.auth.signOut();
    } catch (_) {}
    set({ session: null, user: null, loading: false });
  },
}));
