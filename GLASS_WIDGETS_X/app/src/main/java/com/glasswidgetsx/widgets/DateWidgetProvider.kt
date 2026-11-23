package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val now = Date()
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_date).apply {
                setTextViewText(R.id.date_day, SimpleDateFormat("dd", Locale.getDefault()).format(now))
                setTextViewText(R.id.date_month, SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(now).uppercase(Locale.getDefault()))
                setTextViewText(R.id.date_weekday, SimpleDateFormat("EEEE", Locale.getDefault()).format(now).uppercase(Locale.getDefault()))
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.date_day, R.id.date_month, R.id.date_weekday)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
