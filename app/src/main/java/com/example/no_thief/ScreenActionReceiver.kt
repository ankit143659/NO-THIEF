package com.example.no_thief

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenActionReceiver : BroadcastReceiver() {

    private var lastScreenOnTime: Long = 0
    private val doublePressThreshold: Long = 500  // Time in milliseconds to detect double press

    override fun onReceive(context: Context, intent: Intent) {
        val currentTime = System.currentTimeMillis()

        if (intent.action == "android.intent.action.SCREEN_ON") {
            if (currentTime - lastScreenOnTime < doublePressThreshold) {
                // Double press detected, start the service
                val serviceIntent = Intent(context, ScreenActionService::class.java)
                context.startService(serviceIntent)
            }
            lastScreenOnTime = currentTime
        }
    }
}
