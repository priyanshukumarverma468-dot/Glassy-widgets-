package com.glasswidgetsx.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.glasswidgetsx.R

class NoteWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(context, manager, it) }
    }

    companion object {
        private const val PREFS = "glass_note_widget"
        fun update(context: Context, manager: AppWidgetManager, id: Int) {
            val note = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("note_$id", "Tap to write your note") ?: "Tap to write your note"
            val intent = Intent(context, NoteConfigActivity::class.java).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            val pending = PendingIntent.getActivity(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            val views = RemoteViews(context.packageName, R.layout.widget_note).apply {
                setTextViewText(R.id.note_text, note)
                setOnClickPendingIntent(R.id.note_text, pending)
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.note_text)
            }
            manager.updateAppWidget(id, views)
        }
        fun refreshAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, NoteWidgetProvider::class.java))
            ids.forEach { update(context, manager, it) }
        }
    }
}
