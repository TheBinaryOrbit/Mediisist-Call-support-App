package com.example.call_support

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.call_support.utils.AppPreferences
import kotlin.jvm.java



class OverlayBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!AppPreferences.isOnline(context)) return

        val name = intent.getStringExtra("name") ?: "Unknown"
        val address = intent.getStringExtra("address") ?: "Unknown"
        val phone = intent.getStringExtra("phone") ?: "0000000000"

        val overlayIntent = Intent(context, OverlayActivity::class.java).apply {
            putExtra("name", name)
            putExtra("address", address)
            putExtra("phone", phone)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(overlayIntent)
    }
}
