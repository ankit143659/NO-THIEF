package com.example.no_thief

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat

class PowerButtonReceiver : BroadcastReceiver() {
    private var lastPowerPressTime: Long = 0
    private val DOUBLE_PRESS_INTERVAL = 500 // milliseconds

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PowerButtonReceiver", "Power button pressed")
        if (intent.action == Intent.ACTION_SCREEN_OFF || intent.action == Intent.ACTION_SCREEN_ON) {
            val currentTime = SystemClock.elapsedRealtime()
            if (currentTime - lastPowerPressTime < DOUBLE_PRESS_INTERVAL) {
                // Detected double press
                Log.d("PowerButtonReceiver", "Double press detected")
                startForegroundService(context)
            }
            lastPowerPressTime = currentTime
        }
    }

    private fun startForegroundService(context: Context) {
        val serviceIntent = Intent(context, PowerButtonPressed::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
