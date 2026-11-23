package com.glasswidgetsx

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder

class TimerForegroundService : Service() {
    private var timer: CountDownTimer? = null
    private val channelId = "glass_timer"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seconds = intent?.getLongExtra("seconds", 0L) ?: 0L
        if (seconds <= 0L) { stopSelf(); return START_NOT_STICKY }
        createChannel()
        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Glass Widgets X timer")
            .setContentText("Countdown is running")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()
        startForeground(4102, notification)
        timer?.cancel()
        timer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millis: Long) {
                val mins = (millis / 60000)
                val secs = (millis / 1000) % 60
                val updated = Notification.Builder(this@TimerForegroundService, channelId)
                    .setContentTitle("Glass Widgets X timer")
                    .setContentText(String.format("%02d:%02d remaining", mins, secs))
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                    .setOngoing(true).build()
                val manager = getSystemService(NotificationManager::class.java)
                manager.notify(4102, updated)
            }
            override fun onFinish() {
                val done = Notification.Builder(this@TimerForegroundService, channelId)
                    .setContentTitle("Timer finished").setContentText("Your countdown is complete.")
                    .setSmallIcon(android.R.drawable.ic_lock_idle_alarm).build()
                getSystemService(NotificationManager::class.java).notify(4103, done)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }.start()
        return START_NOT_STICKY
    }
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(channelId, "Countdown timer", NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { timer?.cancel(); super.onDestroy() }
}
