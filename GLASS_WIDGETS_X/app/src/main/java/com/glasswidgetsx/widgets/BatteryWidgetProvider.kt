package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.RemoteViews
import com.glasswidgetsx.R

class BatteryWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val battery = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val percent = if (level >= 0 && scale > 0) (level * 100 / scale) else 0
        val status = battery?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val state = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Fully charged"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "On battery"
            else -> "Battery status"
        }
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_battery).apply {
                setTextViewText(R.id.battery_percent, "$percent%")
                setTextViewText(R.id.battery_state, state)
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.battery_percent, R.id.battery_state)
            }
            manager.updateAppWidget(id, views)
        }
    }
}
