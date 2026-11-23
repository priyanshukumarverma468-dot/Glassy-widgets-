package com.glasswidgetsx.widgets

import android.app.PendingIntent
import android.app.SearchManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.glasswidgetsx.R

class SearchWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val search = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, "")
            }
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
