package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class KoreFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo FCM Token recibido: $token")
        
        saveFcmToken(this, token)
        subscribeToDefaultTopics()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Mensaje FCM recibido de: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "KoreLife Alerta"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "Tienes una nueva notificación desde el Panel Administrativo"

        val type = remoteMessage.data["type"] ?: "general"

        showPushNotification(this, title, body, type, remoteMessage.data)
    }

    private fun subscribeToDefaultTopics() {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Suscrito exitosamente al tópico 'all_users'")
                    }
                }
            FirebaseMessaging.getInstance().subscribeToTopic("transacciones")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Suscrito exitosamente al tópico 'transacciones'")
                    }
                }
            FirebaseMessaging.getInstance().subscribeToTopic("anuncios")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Suscrito exitosamente al tópico 'anuncios'")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error suscribiendo a tópicos FCM", e)
        }
    }

    companion object {
        private const val TAG = "KoreLifeFCM"
        private const val PREFS_NAME = "korelife_fcm_prefs"
        private const val KEY_FCM_TOKEN = "fcm_registration_token"

        const val CHANNEL_FCM_ID = "korelife_admin_fcm"
        const val CHANNEL_FCM_NAME = "Notificaciones Push Administrativas"

        fun saveFcmToken(context: Context, token: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        }

        fun getSavedFcmToken(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_FCM_TOKEN, null)
        }

        fun showPushNotification(
            context: Context,
            title: String,
            body: String,
            type: String = "general",
            data: Map<String, String>? = null
        ) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_FCM_ID,
                    CHANNEL_FCM_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notificaciones push en tiempo real enviadas desde el panel administrativo de KoreLife"
                    enableVibration(true)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("fcm_type", type)
                data?.forEach { (key, value) ->
                    putExtra("fcm_$key", value)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationId = (System.currentTimeMillis() % 100000).toInt()

            val notification = NotificationCompat.Builder(context, CHANNEL_FCM_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(0xFF10B981.toInt())
                .build()

            notificationManager.notify(notificationId, notification)
        }

        fun initializeFcm(context: Context, onTokenReceived: ((String) -> Unit)? = null) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val token = task.result
                        Log.d(TAG, "Token FCM inicial obtenido: $token")
                        saveFcmToken(context, token)
                        onTokenReceived?.invoke(token)
                    } else {
                        Log.w(TAG, "No se pudo obtener el token FCM inicial", task.exception)
                    }
                }

                FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                FirebaseMessaging.getInstance().subscribeToTopic("transacciones")
                FirebaseMessaging.getInstance().subscribeToTopic("anuncios")
            } catch (e: Exception) {
                Log.e(TAG, "Error inicializando FCM", e)
            }
        }
    }
}
