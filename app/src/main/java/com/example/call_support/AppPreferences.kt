// AppPreferences.kt
package com.example.call_support.utils

import android.content.Context

object AppPreferences {
    private const val PREF_NAME = "call_support_prefs"
    private const val KEY_ONLINE = "is_online"

    fun setOnline(context: Context, isOnline: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONLINE, isOnline).apply()
    }

    fun isOnline(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONLINE, true)
    }
}
