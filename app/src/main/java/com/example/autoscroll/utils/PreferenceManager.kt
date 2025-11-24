package com.example.autoscroll.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("AutoScrollPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TIMER_SECONDS = "timer_seconds"
        private const val DEFAULT_TIMER_SECONDS = 5
    }

    var timerSeconds: Int
        get() = sharedPreferences.getInt(KEY_TIMER_SECONDS, DEFAULT_TIMER_SECONDS)
        set(value) {
            sharedPreferences.edit().putInt(KEY_TIMER_SECONDS, value).apply()
        }
}
