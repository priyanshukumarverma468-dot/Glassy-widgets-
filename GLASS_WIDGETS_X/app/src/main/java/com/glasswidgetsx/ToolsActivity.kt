package com.glasswidgetsx

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class ToolsActivity : Activity() {
    private val lavender = Color.rgb(216, 197, 255)
    private var running = false
    private var elapsedBefore = 0L
    private var startedAt = 0L
    private var timer: CountDownTimer? = null
    private var remainingMs = 0L
    private lateinit var watchText: TextView
    private lateinit var timerText: TextView
    private lateinit var timerInput: EditText
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val tick = object : Runnable {
        override fun run() {
            if (running) {
                val elapsed = elapsedBefore + (SystemClock.elapsedRealtime() - startedAt)
                watchText.text = formatStopwatch(elapsed)
                handler.postDelayed(this, 40L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 9, 13)
        window.navigationBarColor = Color.rgb(8, 9, 13)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(28), dp(22), dp(24))
            setBackgroundColor(Color.rgb(8, 9, 13))
        }
        page.addView(label("GLASS WIDGETS X  /  TOOLS", 12, lavender, true))
        page.addView(label("Stopwatch", 29, Color.WHITE, true).apply {
            setPadding(0, dp(22), 0, dp(8))
        })
        watchText = label("00:00.00", 38, Color.WHITE, true)
        page.addView(watchText)
        val watchButtons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        watchButtons.addView(button("START") { startStopwatch() }, weight())
        watchButtons.addView(button("PAUSE") { pauseStopwatch() }, weight())
        watchButtons.addView(button("RESET") { resetStopwatch() }, weight())
        page.addView(watchButtons, fullWidth(dp(54), dp(16)))

        page.addView(label("Countdown Timer", 24, Color.WHITE, true).apply {
            setPadding(0, dp(34), 0, dp(8))
        })
        timerText = label("00:00", 34, lavender, true)
        page.addView(timerText)
        timerInput = EditText(this).apply {
            hint = "Enter duration in seconds"
            setHintTextColor(0xFF8F899B.toInt())
            setTextColor(Color.WHITE)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setSingleLine(true)
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(0x22FFFFFF, 0x55FFFFFF)
        }
        page.addView(timerInput, fullWidth(dp(52), dp(14)))
        val timerButtons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        timerButtons.addView(button("START") { startCountdown() }, weight())
        timerButtons.addView(button("CANCEL") { cancelCountdown() }, weight())
        timerButtons.addView(button("RUN BACKGROUND") { startBackgroundTimer() }, weight())
        page.addView(timerButtons, fullWidth(dp(54), dp(10)))
        page.addView(label("Timer continues while this screen is open. Android may pause app work when the app is backgrounded.", 12, 0xFFAAA5B5.toInt(), false).apply {
            setPadding(0, dp(18), 0, 0)
        })
        setContentView(page)
    }

    private fun startStopwatch() {
        if (running) return
        running = true
        startedAt = SystemClock.elapsedRealtime()
        handler.removeCallbacks(tick)
        handler.post(tick)
    }

    private fun pauseStopwatch() {
        if (!running) return
        elapsedBefore += SystemClock.elapsedRealtime() - startedAt
        running = false
        handler.removeCallbacks(tick)
        watchText.text = formatStopwatch(elapsedBefore)
    }

    private fun resetStopwatch() {
        running = false
        elapsedBefore = 0L
        handler.removeCallbacks(tick)
        watchText.text = "00:00.00"
    }

    private fun startCountdown() {
        if (timer != null) return
        val seconds = timerInput.text.toString().toLongOrNull()
        if (seconds == null || seconds !in 1..86400) {
            Toast.makeText(this, "Enter a time from 1 to 86400 seconds.", Toast.LENGTH_SHORT).show()
            return
        }
        remainingMs = seconds * 1000L
        timer = object : CountDownTimer(remainingMs, 250L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMs = millisUntilFinished
                timerText.text = formatCountdown(millisUntilFinished)
            }
            override fun onFinish() {
                remainingMs = 0L
                timerText.text = "00:00"
                timer = null
                Toast.makeText(this@ToolsActivity, "Countdown finished", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun startBackgroundTimer() {
        val seconds = timerInput.text.toString().toLongOrNull()
        if (seconds == null || seconds !in 1..86400) {
            Toast.makeText(this, "Enter a time from 1 to 86400 seconds.", Toast.LENGTH_SHORT).show()
            return
        }
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 83)
            Toast.makeText(this, "Allow notifications, then start the timer again.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = android.content.Intent(this, TimerForegroundService::class.java)
            .putExtra("seconds", seconds)
        if (android.os.Build.VERSION.SDK_INT >= 26) startForegroundService(intent) else startService(intent)
        Toast.makeText(this, "Background countdown started. Allow notifications if prompted.", Toast.LENGTH_LONG).show()
    }

    private fun cancelCountdown() {
        timer?.cancel()
        timer = null
        remainingMs = 0L
        timerText.text = "00:00"
    }

    override fun onDestroy() {
        handler.removeCallbacks(tick)
        timer?.cancel()
        super.onDestroy()
    }

    private fun formatStopwatch(ms: Long): String {
        val centis = ms / 10
        val minutes = centis / 6000
        val seconds = (centis / 100) % 60
        val hundredths = centis % 100
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, hundredths)
    }

    private fun formatCountdown(ms: Long): String {
        val totalSeconds = (ms + 999) / 1000
        return String.format(Locale.getDefault(), "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
    }

    private fun label(value: String, size: Int, color: Int, bold: Boolean) =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
        }

    private fun button(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 11f
        setTextColor(0xFF17131F.toInt())
        background = rounded(lavender, lavender)
        setOnClickListener { action() }
    }

    private fun rounded(fill: Int, stroke: Int) = GradientDrawable().apply {
        cornerRadius = dp(15).toFloat()
        setColor(fill)
        setStroke(dp(1), stroke)
    }

    private fun weight() = LinearLayout.LayoutParams(0, -1, 1f).apply {
        marginStart = dp(4); marginEnd = dp(4)
    }

    private fun fullWidth(height: Int, top: Int) = LinearLayout.LayoutParams(-1, height).apply {
        topMargin = top
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
