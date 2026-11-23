package com.glasswidgetsx.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import android.widget.RemoteViews
import com.glasswidgetsx.R

class MusicWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }

    companion object {
        fun update(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_music)
            fun mediaIntent(keyCode: Int, requestCode: Int) {
                val intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
                    component = ComponentName(context, MediaButtonReceiver::class.java)
                    putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
                }
                val pending = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(
                    if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) R.id.music_previous
                    else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) R.id.music_next
                    else R.id.music_play,
                    pending
                )
            }
            mediaIntent(KeyEvent.KEYCODE_MEDIA_PREVIOUS, id * 10 + 1)
            mediaIntent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, id * 10 + 2)
            mediaIntent(KeyEvent.KEYCODE_MEDIA_NEXT, id * 10 + 3)
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val active = audioManager.isMusicActive
            views.setTextViewText(R.id.music_title, if (active) "Playing music" else "Music")
            views.setTextViewText(R.id.music_artist, "Media-session controls")
            views.setTextViewText(R.id.music_play, if (active) "PAUSE" else "PLAY")
            WidgetThemeSupport.apply(context, views, R.id.widget_root, R.id.music_title, R.id.music_artist)
            manager.updateAppWidget(id, views)
        }
    }
}

class MediaButtonReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.dispatchMediaKeyEvent(event)
    }
}
