package com.aarav.screentimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

/** Turns the overlay back on after the phone restarts (if it was on before). */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (TimeStore.isEnabled(context) && Settings.canDrawOverlays(context)) {
            context.startForegroundService(Intent(context, OverlayService::class.java))
        }
    }
}
