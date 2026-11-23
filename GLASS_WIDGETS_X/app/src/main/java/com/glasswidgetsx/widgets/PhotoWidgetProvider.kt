package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.widget.RemoteViews
import com.glasswidgetsx.R

class PhotoWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val value = context.getSharedPreferences("glass_photo_widget", Context.MODE_PRIVATE)
                .getString("photo_$id", null)
            update(context, manager, id, value?.let { Uri.parse(it) })
        }
    }
    companion object {
        fun update(context: Context, manager: AppWidgetManager, id: Int, uri: Uri?) {
            val views = RemoteViews(context.packageName, R.layout.widget_photo)
            WidgetThemeSupport.apply(context, views, R.id.widget_root, R.id.photo_placeholder)
            if (uri != null) {
                views.setImageViewUri(R.id.photo_image, uri)
                views.setViewVisibility(R.id.photo_placeholder, android.view.View.GONE)
            } else {
                views.setViewVisibility(R.id.photo_placeholder, android.view.View.VISIBLE)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
