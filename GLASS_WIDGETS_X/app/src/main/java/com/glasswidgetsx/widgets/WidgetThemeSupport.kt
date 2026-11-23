package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.glasswidgetsx.R
import com.glasswidgetsx.ThemeManager

object WidgetThemeSupport {
    fun apply(context: Context, views: RemoteViews, rootId: Int, vararg textIds: Int) {
        val theme = ThemeManager.get(context)
        val alpha = (theme.opacity.coerceIn(5, 75) * 255 / 100) shl 24
        val background = (theme.background and 0x00FFFFFF) or alpha
        views.setInt(rootId, "setBackgroundColor", background)
        textIds.forEach { views.setTextColor(it, theme.text) }
    }

    fun refreshAll(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val providers = listOf(
            ClockWidgetProvider::class.java,
            AnalogClockWidgetProvider::class.java,
            BatteryWidgetProvider::class.java,
            DateWidgetProvider::class.java,
            WeatherWidgetProvider::class.java,
            CalendarWidgetProvider::class.java,
            NoteWidgetProvider::class.java,
            PhotoWidgetProvider::class.java,
            SearchWidgetProvider::class.java,
            QuickLaunchWidgetProvider::class.java,
            MusicWidgetProvider::class.java
        )
        providers.forEach { provider ->
            val ids = manager.getAppWidgetIds(ComponentName(context, provider))
            if (ids.isNotEmpty()) {
                context.sendBroadcast(Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                    .setComponent(ComponentName(context, provider))
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids))
            }
        }
    }
}
