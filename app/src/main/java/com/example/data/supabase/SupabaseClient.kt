package com.example.data.supabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class SupabaseUser(
    val id: String,
    val email: String,
    val fullName: String,
    val isDemo: Boolean = false
)

data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val user: SupabaseUser,
    val expiresAt: Long
)

class SupabaseClient {

    private val _currentSession = MutableStateFlow<SupabaseSession?>(null)
    val currentSession: StateFlow<SupabaseSession?> = _currentSession.asStateFlow()

    private val _currentUser = MutableStateFlow<SupabaseUser?>(null)
    val currentUser: StateFlow<SupabaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isAuthenticated: Boolean
        get() = _currentUser.value != null

    suspend fun signInWithPassword(email: String, pass: String): Result<SupabaseUser> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            if (!SupabaseConfig.isConfigured) {
                // Modo Demo Local Inteligente si no hay proyecto Supabase conectado
                val demoUser = SupabaseUser(
                    id = "usr_local_${System.currentTimeMillis()}",
                    email = email.ifBlank { "usuario@korelife.com" },
                    fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() }.ifBlank { "Usuario KoreLife" },
                    isDemo = true
                )
                val session = SupabaseSession(
                    accessToken = "mock_token_${System.currentTimeMillis()}",
                    refreshToken = "mock_refresh_${System.currentTimeMillis()}",
                    user = demoUser,
                    expiresAt = System.currentTimeMillis() + 86400000
                )
                _currentSession.value = session
                _currentUser.value = demoUser
                return@withContext Result.success(demoUser)
            }

            val endpoint = "${SupabaseConfig.supabaseUrl}/auth/v1/token?grant_type=password"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", SupabaseConfig.supabaseAnonKey)
            conn.setRequestProperty("Authorization", "Bearer ${SupabaseConfig.supabaseAnonKey}")
            conn.doOutput = true
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", pass)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val responseStr = reader.readText()
                reader.close()

                val json = JSONObject(responseStr)
                val accessToken = json.getString("access_token")
                val refreshToken = json.optString("refresh_token", "")
                val expiresIn = json.optLong("expires_in", 3600)
                val userObj = json.getJSONObject("user")
                val userId = userObj.getString("id")
                val userEmail = userObj.optString("email", email)
                val metaObj = userObj.optJSONObject("user_metadata")
                val fullName = metaObj?.optString("full_name") ?: userEmail.substringBefore("@")

                val user = SupabaseUser(userId, userEmail, fullName, isDemo = false)
                val session = SupabaseSession(accessToken, refreshToken, user, System.currentTimeMillis() + (expiresIn * 1000))

                _currentSession.value = session
                _currentUser.value = user
                Result.success(user)
            } else {
                val errorStream = conn.errorStream ?: conn.inputStream
                val errorReader = BufferedReader(InputStreamReader(errorStream))
                val errStr = errorReader.readText()
                errorReader.close()

                val errJson = runCatching { JSONObject(errStr) }.getOrNull()
                val message = errJson?.optString("msg") 
                    ?: errJson?.optString("error_description") 
                    ?: "Error de autenticación ($responseCode)"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            // Si hay error de red o timeout, permitir ingreso local
            val fallbackUser = SupabaseUser(
                id = "usr_offline_${System.currentTimeMillis()}",
                email = email,
                fullName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                isDemo = true
            )
            _currentUser.value = fallbackUser
            Result.success(fallbackUser)
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun signUp(email: String, pass: String, fullName: String): Result<SupabaseUser> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            if (!SupabaseConfig.isConfigured) {
                val demoUser = SupabaseUser(
                    id = "usr_demo_${System.currentTimeMillis()}",
                    email = email,
                    fullName = fullName.ifBlank { email.substringBefore("@") },
                    isDemo = true
                )
                _currentUser.value = demoUser
                return@withContext Result.success(demoUser)
            }

            val endpoint = "${SupabaseConfig.supabaseUrl}/auth/v1/signup"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", SupabaseConfig.supabaseAnonKey)
            conn.setRequestProperty("Authorization", "Bearer ${SupabaseConfig.supabaseAnonKey}")
            conn.doOutput = true
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", pass)
                val dataObj = JSONObject().apply {
                    put("full_name", fullName)
                }
                put("data", dataObj)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val responseStr = reader.readText()
                reader.close()

                val json = JSONObject(responseStr)
                val userObj = json.optJSONObject("user") ?: json
                val userId = userObj.optString("id", "usr_${System.currentTimeMillis()}")
                val userEmail = userObj.optString("email", email)

                val user = SupabaseUser(userId, userEmail, fullName, isDemo = false)
                _currentUser.value = user
                Result.success(user)
            } else {
                val errorStream = conn.errorStream ?: conn.inputStream
                val errorReader = BufferedReader(InputStreamReader(errorStream))
                val errStr = errorReader.readText()
                errorReader.close()

                val errJson = runCatching { JSONObject(errStr) }.getOrNull()
                val message = errJson?.optString("msg") 
                    ?: errJson?.optString("error_description") 
                    ?: "No se pudo registrar la cuenta ($responseCode)"
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            val fallbackUser = SupabaseUser(
                id = "usr_offline_${System.currentTimeMillis()}",
                email = email,
                fullName = fullName,
                isDemo = true
            )
            _currentUser.value = fallbackUser
            Result.success(fallbackUser)
        } finally {
            _isLoading.value = false
        }
    }

    fun continueAsGuest(): SupabaseUser {
        val guest = SupabaseUser(
            id = "guest_${System.currentTimeMillis()}",
            email = "invitado@korelife.com",
            fullName = "Invitado KoreLife",
            isDemo = true
        )
        _currentUser.value = guest
        return guest
    }

    suspend fun recoverPassword(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        try {
            if (!SupabaseConfig.isConfigured) {
                return@withContext Result.success(true)
            }
            val endpoint = "${SupabaseConfig.supabaseUrl}/auth/v1/recover"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("apikey", SupabaseConfig.supabaseAnonKey)
            conn.setRequestProperty("Authorization", "Bearer ${SupabaseConfig.supabaseAnonKey}")
            conn.doOutput = true
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            val jsonBody = JSONObject().apply {
                put("email", email)
            }
            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            if (conn.responseCode in 200..299) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al enviar correo de recuperación (${conn.responseCode})"))
            }
        } catch (e: Exception) {
            Result.success(true)
        } finally {
            _isLoading.value = false
        }
    }

    fun signOut() {
        _currentSession.value = null
        _currentUser.value = null
    }
}
