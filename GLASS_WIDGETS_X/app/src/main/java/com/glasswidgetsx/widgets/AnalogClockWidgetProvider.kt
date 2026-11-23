package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.glasswidgetsx.R

class AnalogClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_analog_clock)
            WidgetThemeSupport.apply(context, views, R.id.widget_root)
            manager.updateAppWidget(id, views)
        }
    }
}
