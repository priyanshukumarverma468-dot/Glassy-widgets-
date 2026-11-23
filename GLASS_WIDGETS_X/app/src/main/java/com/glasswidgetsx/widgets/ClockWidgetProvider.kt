package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            }
            manager.updateAppWidget(id, views)
        }
    }
}
