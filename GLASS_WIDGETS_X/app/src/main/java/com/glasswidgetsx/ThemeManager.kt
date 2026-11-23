package com.glasswidgetsx

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import org.json.JSONObject

data class WidgetTheme(
    val name: String = "Lavender Glass",
    val accent: Int = 0xFFD8C5FF.toInt(),
    val text: Int = 0xFFFFFFFF.toInt(),
    val secondary: Int = 0xFFCAC5D5.toInt(),
    val background: Int = 0xFF171521.toInt(),
    val opacity: Int = 24,
    val radius: Int = 22,
    val textSize: Int = 34,
    val clockStyle: String = "Minimal"
)

object ThemeManager {
    const val PREFS = "glass_studio"
    const val ACTION_REFRESH_WIDGETS = "com.glasswidgetsx.UPDATE_WIDGETS"

    fun get(context: Context): WidgetTheme {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return WidgetTheme(
            p.getString("theme_name", "Lavender Glass") ?: "Lavender Glass",
            p.getInt("accent", 0xFFD8C5FF.toInt()),
            p.getInt("text_color", 0xFFFFFFFF.toInt()),
            p.getInt("secondary_color", 0xFFCAC5D5.toInt()),
            p.getInt("background", 0xFF171521.toInt()),
            p.getInt("opacity", 24),
            p.getInt("radius", 22),
            p.getInt("text_size", 34),
            p.getString("clock_style", "Minimal") ?: "Minimal"
        )
    }

    fun save(context: Context, name: String, theme: WidgetTheme) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit()
            .putString("theme_name", name)
            .putInt("accent", theme.accent)
            .putInt("text_color", theme.text)
            .putInt("secondary_color", theme.secondary)
            .putInt("background", theme.background)
            .putInt("opacity", theme.opacity)
            .putInt("radius", theme.radius)
            .putInt("text_size", theme.textSize)
            .putString("clock_style", theme.clockStyle)
            .apply()
        val saved = p.getStringSet("saved_theme_names", linkedSetOf())?.toMutableSet() ?: mutableSetOf()
        saved.add(name)
        p.edit().putStringSet("saved_theme_names", saved).apply()
        val data = JSONObject()
            .put("accent", theme.accent).put("text", theme.text)
            .put("secondary", theme.secondary).put("background", theme.background)
            .put("opacity", theme.opacity).put("radius", theme.radius)
            .put("textSize", theme.textSize).put("clockStyle", theme.clockStyle)
        p.edit().putString("theme_json_$name", data.toString()).apply()
    }

    fun load(context: Context, name: String): WidgetTheme? {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = p.getString("theme_json_$name", null) ?: return null
        return try {
            val o = JSONObject(raw)
            val theme = WidgetTheme(
                name, o.optInt("accent"), o.optInt("text", 0xFFFFFFFF.toInt()),
                o.optInt("secondary", 0xFFCAC5D5.toInt()),
                o.optInt("background", 0xFF171521.toInt()), o.optInt("opacity", 24),
                o.optInt("radius", 22), o.optInt("textSize", 34),
                o.optString("clockStyle", "Minimal")
            )
            save(context, name, theme)
            theme
        } catch (_: Exception) {
            null
        }
    }

    fun reset(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        p.edit()
            .putString("theme_name", "Lavender Glass")
            .putInt("accent", 0xFFD8C5FF.toInt())
            .putInt("text_color", 0xFFFFFFFF.toInt())
            .putInt("secondary_color", 0xFFCAC5D5.toInt())
            .putInt("background", 0xFF171521.toInt())
            .putInt("opacity", 24).putInt("radius", 22)
            .putInt("text_size", 34).putString("clock_style", "Minimal")
            .apply()
        refreshAll(context)
    }

    fun applyToViews(views: RemoteViews, context: Context, vararg textIds: Int) {
        val theme = get(context)
        val alpha = (theme.opacity.coerceIn(5, 75) * 255 / 100)
        val bg = (theme.background and 0x00FFFFFF) or (alpha shl 24)
        textIds.forEach { views.setTextColor(it, theme.text) }
        val accentIds = intArrayOf(
            0x0 // marker; providers add their own accent IDs
        )
    }

    fun refreshAll(context: Context) {
        context.sendBroadcast(Intent(ACTION_REFRESH_WIDGETS).setPackage(context.packageName))
    }

    fun savedNames(context: Context): List<String> =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet("saved_theme_names", emptySet())?.sorted() ?: emptyList()
}
