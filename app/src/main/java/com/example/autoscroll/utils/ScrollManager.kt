package com.example.autoscroll.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.autoscroll.AutoScrollService
import android.util.Log

class ScrollManager(private val context: Context) {

    private val preferenceManager = PreferenceManager(context)
    private val handler = Handler(Looper.getMainLooper())
    private var isScrolling = false

    private val scrollRunnable = object : Runnable {
        override fun run() {
            if (isScrolling) {
                performScroll()
                handler.postDelayed(this, preferenceManager.timerSeconds * 1000L)
            }
        }
    }

    fun startScrolling() {
        if (!isScrolling) {
            isScrolling = true
            handler.post(scrollRunnable)
            Log.d("ScrollManager", "Started Scrolling")
        }
    }

    fun stopScrolling() {
        isScrolling = false
        handler.removeCallbacks(scrollRunnable)
        Log.d("ScrollManager", "Stopped Scrolling")
    }

    private fun performScroll() {
        val service = AutoScrollService.instance
        if (service != null) {
            // Default to scroll down (swipe up gesture)
            service.performScroll(isScrollDown = true)
        } else {
            Log.e("ScrollManager", "AutoScrollService is not connected")
        }
    }

    fun increaseTimer() {
        preferenceManager.timerSeconds++
    }

    fun decreaseTimer() {
        if (preferenceManager.timerSeconds > 1) {
            preferenceManager.timerSeconds--
        }
    }

    fun setTimerValue(seconds: Int) {
        preferenceManager.timerSeconds = seconds
    }

    fun getTimerValue(): Int {
        return preferenceManager.timerSeconds
    }
}
