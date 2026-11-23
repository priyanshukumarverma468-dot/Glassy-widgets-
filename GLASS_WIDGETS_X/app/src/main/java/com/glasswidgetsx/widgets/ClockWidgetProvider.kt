package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.format.DateFormat
import android.app.PendingIntent
import android.content.Intent
import android.provider.AlarmClock

class ClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id -> update(context, manager, id) }
    }

    companion object {
        fun update(context: Context, manager: AppWidgetManager, id: Int) {
            val now = Date()
            val views = RemoteViews(context.packageName, R.layout.widget_clock).apply {
                setTextViewText(R.id.clock_date, SimpleDateFormat("EEEE • dd MMM", Locale.getDefault()).format(now).uppercase(Locale.getDefault()))
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.clock_time, R.id.clock_date)
                val theme = com.glasswidgetsx.ThemeManager.get(context)
                setTextViewTextSize(R.id.clock_time, android.util.TypedValue.COMPLEX_UNIT_SP, theme.textSize.toFloat())
                setTextViewTextSize(R.id.clock_date, android.util.TypedValue.COMPLEX_UNIT_SP, 12f)
                val prefs = context.getSharedPreferences("glass_studio", Context.MODE_PRIVATE)
                val use24 = prefs.getBoolean("clock_24_hour", !DateFormat.is24HourFormat(context))
                val showSeconds = prefs.getBoolean("clock_show_seconds", false)
                setCharSequence(R.id.clock_time, "setFormat12Hour", if (showSeconds) "hh:mm:ss" else "hh:mm")
                setCharSequence(R.id.clock_time, "setFormat24Hour", if (showSeconds) "HH:mm:ss" else "HH:mm")
                val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                val alarmPending = PendingIntent.getActivity(
                    context,
                    id,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.clock_time, alarmPending)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
