package com.glasswidgetsx.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.RemoteViews
import com.glasswidgetsx.R

class QuickLaunchWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_quick_launch)
            launchIntent(context, views, R.id.quick_camera, "android.media.action.STILL_IMAGE_CAMERA", id)
            launchIntent(context, views, R.id.quick_settings, Settings.ACTION_SETTINGS, id + 1)
            launchIntent(context, views, R.id.quick_search, "android.intent.action.ASSIST", id + 2)
            launchIntent(context, views, R.id.quick_gallery, "android.intent.action.PICK", id + 3)
            WidgetThemeSupport.apply(context, views, R.id.widget_root)
            manager.updateAppWidget(id, views)
        }
    }

    private fun launchIntent(context: Context, views: RemoteViews, viewId: Int, action: String, request: Int) {
        val intent = Intent(action).apply {
            if (action == "android.intent.action.PICK") {
                setType("image/*")
            }
        }
        val pending = PendingIntent.getActivity(
            context, request, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(viewId, pending)
    }
}
