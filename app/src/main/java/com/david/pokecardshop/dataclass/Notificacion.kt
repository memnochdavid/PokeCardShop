package com.david.pokecardshop.dataclass

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.ui.input.key.key
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.david.pokecardshop.MainActivity
import com.david.pokecardshop.R
import com.david.pokecardshop.refBBDD
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMessagingService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"] ?: "Default Title"
            val body = remoteMessage.data["body"] ?: "Default Body"
            val notificacion = Notificacion(titulo = title, texto = body)
            createNotificationChannel(this, notificacion)
            sendNotification(this, notificacion)
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            val notificacion = Notificacion(
                titulo = it.title ?: "Default Title",
                texto = it.body ?: "Default Body"
            )
            createNotificationChannel(this, notificacion)
            sendNotification(this, notificacion)
        }
    }

    private fun needsToBeScheduled(): Boolean {
        // Implement your logic here
        return false
    }

    private fun scheduleJob() {
        // Implement WorkManager logic here
        Log.d(TAG, "Job scheduled")
    }

    private fun handleNow() {
        Log.d(TAG, "Handling message now")
    }
}

data class Notificacion(
    var notificacion_id: String = "",
    var channelId: String = "my_channel_id",
    var notificationId: Int = Random.nextInt(), // Generate a random ID
    var titulo: String = "Título",
    var texto: String = "Contenido",
    var importancia: Int = NotificationManager.IMPORTANCE_HIGH
) {
    init {
        notificacion_id = refBBDD.child("tienda").child("notificaciones").push().key
            ?: "default_id"
    }
}


fun sendNotification(
    context: Context,
    notificacion: Notificacion
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("message", "Hello from notification!")
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        notificacion.notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(context, notificacion.channelId)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.icon
            )
        )
        .setContentTitle(notificacion.titulo)
        .setContentText(notificacion.texto)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.icon, "Abrir", pendingIntent)


    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("sendNotification", "Permission POST_NOTIFICATIONS not granted")
            return
        }
        notify(notificacion.notificationId, builder.build())
    }
}

fun createNotificationChannel(
    context: Context,
    notification: Notificacion,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            notification.channelId,
            notification.titulo,
            notification.importancia
        ).apply {
            description = notification.texto
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

