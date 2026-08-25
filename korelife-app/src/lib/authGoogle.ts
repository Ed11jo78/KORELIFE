import * as WebBrowser from 'expo-web-browser';
import * as Linking from 'expo-linking';
import { supabase } from './supabase';

WebBrowser.maybeCompleteAuthSession();

export async function signInWithGoogle() {
  try {
    const redirectUrl = Linking.createURL('auth/callback');
    
    const { data, error } = await supabase.auth.signInWithOAuth({
      provider: 'google',
      options: {
        redirectTo: redirectUrl,
        skipBrowserRedirect: true,
      },
    });

    if (error) {
      return { success: false, error: error.message };
    }

    if (data?.url) {
      const res = await WebBrowser.openAuthSessionAsync(data.url, redirectUrl);
      
      if (res.type === 'success' && res.url) {
        // Extraer los tokens retornados por Supabase en el hash o query string
        const parsedUrl = new URL(res.url);
        let accessToken = parsedUrl.searchParams.get('access_token');
        let refreshToken = parsedUrl.searchParams.get('refresh_token');

        if (!accessToken && parsedUrl.hash) {
          const hashParams = new URLSearchParams(parsedUrl.hash.replace('#', ''));
          accessToken = hashParams.get('access_token');
          refreshToken = hashParams.get('refresh_token');
        }

        if (accessToken && refreshToken) {
          const { data: sessionData, error: sessionError } = await supabase.auth.setSession({
            access_token: accessToken,
            refresh_token: refreshToken,
          });

          if (sessionError) {
            return { success: false, error: sessionError.message };
          }

          return { success: true, session: sessionData.session };
        }
      }
    }

    return { success: false, error: 'Inicio de sesión cancelado o incompleto.' };
  } catch (err: any) {
    return { success: false, error: err?.message || 'Error al conectar con Google' };
  }
}
