package com.aarav.screentimer

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var todayText: TextView
    private lateinit var historyText: TextView
    private lateinit var permButton: Button
    private lateinit var toggleButton: Button

    private val refresh = object : Runnable {
        override fun run() {
            updateUi()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = dp(24f)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(pad, pad * 2, pad, pad)
            setBackgroundColor(Color.parseColor("#FAFAFA"))
        }

        root.addView(TextView(this).apply {
            text = "Screen time today"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(Color.DKGRAY)
        })
        todayText = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(Color.BLACK)
            setPadding(0, dp(8f), 0, dp(24f))
        }
        root.addView(todayText)

        permButton = Button(this).apply {
            text = "1. Allow overlay permission"
            setOnClickListener {
                startActivity(Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ))
            }
        }
        root.addView(permButton)

        toggleButton = Button(this).apply { setOnClickListener { toggle() } }
        root.addView(toggleButton)

        root.addView(TextView(this).apply {
            text = "Last 7 days"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(32f), 0, dp(8f))
        })
        historyText = TextView(this).apply {
            typeface = Typeface.MONOSPACE
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTextColor(Color.BLACK)
        }
        root.addView(historyText)

        root.addView(TextView(this).apply {
            text = "Drag the timer to move it. Tap it to open this screen. " +
                "Time counts only while your phone is unlocked and resets at midnight."
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTextColor(Color.GRAY)
            setPadding(0, dp(32f), 0, 0)
        })

        setContentView(root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        // If it was on before but the service got killed, bring it back.
        if (TimeStore.isEnabled(this) && Settings.canDrawOverlays(this)) startOverlay()
        handler.post(refresh)
    }

    override fun onPause() {
        handler.removeCallbacks(refresh)
        super.onPause()
    }

    private fun toggle() {
        if (!Settings.canDrawOverlays(this)) {
            permButton.performClick()
            return
        }
        if (TimeStore.isEnabled(this)) {
            TimeStore.setEnabled(this, false)
            stopService(Intent(this, OverlayService::class.java))
        } else {
            TimeStore.setEnabled(this, true)
            startOverlay()
        }
        updateUi()
    }

    private fun startOverlay() =
        startForegroundService(Intent(this, OverlayService::class.java))

    private fun updateUi() {
        val hasPerm = Settings.canDrawOverlays(this)
        permButton.isEnabled = !hasPerm
        permButton.text = if (hasPerm) "✓ Overlay permission allowed" else "1. Allow overlay permission"
        toggleButton.text = if (TimeStore.isEnabled(this)) "Turn overlay OFF" else "2. Turn overlay ON"

        todayText.text = TimeStore.format(TimeStore.get(this))

        val today = LocalDate.now()
        historyText.text = (0..6).joinToString("\n") { i ->
            val d = today.minusDays(i.toLong())
            val label = if (i == 0) "Today" else d.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            "%-6s %s".format(label, TimeStore.format(TimeStore.get(this, d)))
        }
    }

    private fun dp(v: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics).toInt()
}
