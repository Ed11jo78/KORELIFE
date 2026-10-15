package com.example.data.supabase

/**
 * Supabase configuration file for KoreLife.
 * Configured from credentials provided in .env / .env.example.
 */
object SupabaseConfig {

    // Default project credentials configured from your Supabase project (ref: cwnmxwtqhxgezqzisudj)
    var supabaseUrl: String = "https://cwnmxwtqhxgezqzisudj.supabase.co"
        private set

    var supabaseAnonKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN3bm14d3RxaHhnZXpxemlzdWRqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODcyMzgzODAsImV4cCI6MjEwMjgxNDM4MH0.OViZyoApe8IcP0IY1MB7AqxByG5Jfzds9MEnVu_0r2g"
        private set

    init {
        // Attempt to load from environment if available
        val envUrl = System.getenv("SUPABASE_URL") 
            ?: System.getenv("EXPO_PUBLIC_SUPABASE_URL")
            ?: System.getenv("VITE_SUPABASE_URL")
        val envKey = System.getenv("SUPABASE_ANON_KEY")
            ?: System.getenv("EXPO_PUBLIC_SUPABASE_ANON_KEY")
            ?: System.getenv("VITE_SUPABASE_ANON_KEY")

        if (!envUrl.isNullOrBlank()) {
            supabaseUrl = envUrl.trim()
        }
        if (!envKey.isNullOrBlank()) {
            supabaseAnonKey = envKey.trim()
        }
    }

    /**
     * Update credentials dynamically if supplied at runtime or from settings.
     */
    fun updateCredentials(url: String, key: String) {
        if (url.isNotBlank()) supabaseUrl = url.trim().removeSuffix("/")
        if (key.isNotBlank()) supabaseAnonKey = key.trim()
    }

    /**
     * Checks if a valid real Supabase project URL is configured (not placeholder).
     */
    val isConfigured: Boolean
        get() = supabaseUrl.startsWith("https://") && 
                !supabaseUrl.contains("your-project-ref") && 
                supabaseAnonKey.isNotBlank() && 
                supabaseAnonKey != "your-anon-key"
}
