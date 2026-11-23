package com.glasswidgetsx.widgets

import android.Manifest
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        var title = "Grant calendar permission in Android settings"
        var detail = "Upcoming events will appear here"
        if (context.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            val now = System.currentTimeMillis()
            val projection = arrayOf(CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART)
            val cursor: Cursor? = context.contentResolver.query(
                CalendarContract.Events.CONTENT_URI, projection,
                "${CalendarContract.Events.DTSTART} >= ?", arrayOf(now.toString()),
                "${CalendarContract.Events.DTSTART} ASC"
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    title = it.getString(0) ?: "Untitled event"
                    detail = SimpleDateFormat("EEE, dd MMM • HH:mm", Locale.getDefault()).format(Date(it.getLong(1)))
                } else {
                    title = "No upcoming events"
                    detail = "Your calendar is clear"
                }
            }
        }
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_calendar).apply {
                setTextViewText(R.id.calendar_line1, title)
                setTextViewText(R.id.calendar_line2, detail)
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.calendar_line1, R.id.calendar_line2)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
