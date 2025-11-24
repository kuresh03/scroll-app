package com.example.autoscroll

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.autoscroll.utils.ScrollManager
import android.util.Log

class FloatingControlService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var scrollManager: ScrollManager
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        scrollManager = ScrollManager(this)

        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        windowManager.addView(floatingView, params)

        setupClickListeners(floatingView, params)
    }

    private fun setupClickListeners(view: View, params: WindowManager.LayoutParams) {
        val btnStartStop = view.findViewById<Button>(R.id.btnStartStop)
        val btnIncrease = view.findViewById<Button>(R.id.btnIncrease)
        val btnDecrease = view.findViewById<Button>(R.id.btnDecrease)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)

        val btnPreset = view.findViewById<Button>(R.id.btnPreset)

        updateTimerText(tvTimer)

        btnStartStop.setOnClickListener {
            if (isRunning) {
                stopAutoScroll()
                btnStartStop.text = "Start"
            } else {
                startAutoScroll()
                btnStartStop.text = "Stop"
            }
        }

        btnIncrease.setOnClickListener {
            scrollManager.increaseTimer()
            updateTimerText(tvTimer)
            btnPreset.text = "Preset: Custom"
        }

        btnDecrease.setOnClickListener {
            scrollManager.decreaseTimer()
            updateTimerText(tvTimer)
            btnPreset.text = "Preset: Custom"
        }

        btnPreset.setOnClickListener {
            cyclePresets(btnPreset, tvTimer)
        }

        btnClose.setOnClickListener {
            stopSelf()
        }

        // Add drag listener logic here if needed
        view.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    android.view.MotionEvent.ACTION_UP -> {
                        return true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun startAutoScroll() {
        isRunning = true
        scrollManager.startScrolling()
    }

    private fun stopAutoScroll() {
        isRunning = false
        scrollManager.stopScrolling()
    }

    private fun updateTimerText(textView: TextView) {
        textView.text = "${scrollManager.getTimerValue()}s"
    }

    private fun cyclePresets(btnPreset: Button, tvTimer: TextView) {
        val current = scrollManager.getTimerValue()
        val nextPreset = when (current) {
            5 -> 10 // YouTube Shorts
            10 -> 15 // TikTok
            15 -> 30 // Long
            30 -> 60 // Very Long
            else -> 5 // Default/Instagram
        }
        scrollManager.setTimerValue(nextPreset)
        updateTimerText(tvTimer)
        
        val label = when (nextPreset) {
            5 -> "IG Reels (5s)"
            10 -> "Shorts (10s)"
            15 -> "TikTok (15s)"
            30 -> "Long (30s)"
            60 -> "Min (60s)"
            else -> "Custom"
        }
        btnPreset.text = "Preset: $label"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
        scrollManager.stopScrolling()
    }
}
