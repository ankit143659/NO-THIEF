package com.example.no_thief

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.media.RingtoneManager

class ScreenActionService : Service() {

    companion object {
        private const val CHANNEL_ID = "NoThiefChannel"
        private const val NOTIFICATION_ID = 1
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "No Thief Service",
                NotificationManager.IMPORTANCE_HIGH  // Ensure the notification is visible and makes sound
            ).apply {
                description = "Channel for No Thief service notifications"
                // Set sound for the notification channel
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("NO THIEF is starting")
            .setContentText("You have pressed the power button twice quickly.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)  // Use your app's icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure the notification is high priority
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))  // Set sound for the notification
            .setContentIntent(getPendingIntent())  // Optional: Open app when notification is clicked
            .build()

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service code here
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
