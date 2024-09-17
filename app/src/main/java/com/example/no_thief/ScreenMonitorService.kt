package com.example.no_thief

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenMonitorService : Service() {

    companion object {
        private const val CHANNEL_ID = "NoThiefChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "No Thief Service",
                NotificationManager.IMPORTANCE_HIGH  // Ensure high visibility
            ).apply {
                description = "Channel for No Thief foreground service"
                // Set sound for the notification channel
                val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("No Thief Service")
            .setContentText("Monitoring screen activity")
            .setSmallIcon(R.drawable.baseline_person_outline_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service code here
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources
    }
}
