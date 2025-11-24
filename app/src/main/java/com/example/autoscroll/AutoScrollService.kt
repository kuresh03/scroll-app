package com.example.autoscroll

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.util.Log

class AutoScrollService : AccessibilityService() {

    companion object {
        var instance: AutoScrollService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AutoScrollService", "Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We don't strictly need to listen to events for basic scrolling,
        // but this can be used to detect app changes if needed.
    }

    override fun onInterrupt() {
        Log.d("AutoScrollService", "Service Interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d("AutoScrollService", "Service Destroyed")
    }

    fun performScroll(isScrollDown: Boolean, durationMs: Long = 300L) {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val startY = if (isScrollDown) height * 0.8f else height * 0.2f
        val endY = if (isScrollDown) height * 0.2f else height * 0.8f
        val x = width / 2f

        val path = Path()
        path.moveTo(x, startY)
        path.lineTo(x, endY)

        val builder = GestureDescription.Builder()
        val gestureDescription = builder
            .addStroke(GestureDescription.StrokeDescription(path, 0, durationMs))
            .build()

        dispatchGesture(gestureDescription, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("AutoScrollService", "Scroll Completed")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("AutoScrollService", "Scroll Cancelled")
            }
        }, null)
    }
}
