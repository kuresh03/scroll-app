package com.example.autoscroll

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnEnableAccessibility: Button
    private lateinit var btnEnableOverlay: Button
    private lateinit var btnStartWidget: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEnableAccessibility = findViewById(R.id.btnEnableAccessibility)
        btnEnableOverlay = findViewById(R.id.btnEnableOverlay)
        btnStartWidget = findViewById(R.id.btnStartWidget)

        btnEnableAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        btnEnableOverlay.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivity(intent)
            }
        }

        btnStartWidget.setOnClickListener {
            if (checkOverlayPermission()) {
                startService(Intent(this, FloatingControlService::class.java))
                finish() // Close activity to go to home screen
            } else {
                Toast.makeText(this, "Overlay permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateButtonStates()
    }

    private fun updateButtonStates() {
        val isAccessibilityEnabled = isAccessibilityServiceEnabled()
        val isOverlayEnabled = checkOverlayPermission()

        btnEnableAccessibility.isEnabled = !isAccessibilityEnabled
        btnEnableAccessibility.text = if (isAccessibilityEnabled) "Accessibility Enabled" else "Enable Accessibility Service"

        btnEnableOverlay.isEnabled = !isOverlayEnabled
        btnEnableOverlay.text = if (isOverlayEnabled) "Overlay Enabled" else "Enable Overlay Permission"

        btnStartWidget.isEnabled = isAccessibilityEnabled && isOverlayEnabled
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        if (accessibilityEnabled == 1) {
            val service = "$packageName/${AutoScrollService::class.java.canonicalName}"
            val settingValue = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return settingValue?.contains(service) == true
        }
        return false
    }
}
