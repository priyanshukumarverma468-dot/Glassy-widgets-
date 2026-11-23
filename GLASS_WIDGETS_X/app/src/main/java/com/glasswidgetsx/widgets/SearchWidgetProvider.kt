package com.glasswidgetsx.widgets

import android.app.PendingIntent
import android.app.SearchManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.net.URLEncoder

class SearchWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val prefs = context.getSharedPreferences("glass_studio", Context.MODE_PRIVATE)
            val provider = prefs.getString("search_provider", "https://www.google.com/search?q=")
                ?: "https://www.google.com/search?q="
            val search = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(provider))
            val pending = PendingIntent.getActivity(
                context, id, search,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val views = RemoteViews(context.packageName, R.layout.widget_search).apply {
                setOnClickPendingIntent(R.id.search_button, pending)
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.search_button)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
