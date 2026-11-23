package com.glasswidgetsx.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.glasswidgetsx.R
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class WeatherWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val prefs = context.getSharedPreferences("glass_studio", Context.MODE_PRIVATE)
        val city = prefs.getString("weather_city", "Patna") ?: "Patna"
        ids.forEach { id ->
            val loading = RemoteViews(context.packageName, R.layout.widget_weather).apply {
                setTextViewText(R.id.weather_city, city.uppercase())
                setTextViewText(R.id.weather_temp, "--°")
                setTextViewText(R.id.weather_details, "Updating weather…")
                WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.weather_city, R.id.weather_temp, R.id.weather_details)
            }
            manager.updateAppWidget(id, loading)
        }
        val pending = goAsync()
        Thread {
            try {
                val encoded = java.net.URLEncoder.encode(city, "UTF-8")
                val geo = JSONObject(URL("https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=1&language=en&format=json")
                    .openStream().bufferedReader().use { it.readText() })
                val loc = geo.optJSONArray("results")?.optJSONObject(0) ?: throw Exception("City not found")
                val lat = loc.getDouble("latitude")
                val lon = loc.getDouble("longitude")
                val data = JSONObject(URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,wind_speed_10m")
                    .openStream().bufferedReader().use { it.readText() }).getJSONObject("current")
                val temp = data.optDouble("temperature_2m")
                val humidity = data.optInt("relative_humidity_2m")
                val wind = data.optDouble("wind_speed_10m")
                prefs.edit()
                    .putString("weather_cached_city", loc.optString("name", city))
                    .putString("weather_cached_result", "${temp.toInt()}°C|$humidity|${wind.toInt()}|${loc.optString("name", city)}")
                    .apply()
                ids.forEach { id ->
                    val views = RemoteViews(context.packageName, R.layout.widget_weather).apply {
                        setTextViewText(R.id.weather_city, loc.optString("name", city).uppercase())
                        setTextViewText(R.id.weather_temp, "${temp.toInt()}°C")
                        setTextViewText(R.id.weather_details, "Humidity $humidity%  •  Wind ${wind.toInt()} km/h")
                        WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.weather_city, R.id.weather_temp, R.id.weather_details)
                    }
                    manager.updateAppWidget(id, views)
                }
            } catch (_: Exception) {
                ids.forEach { id ->
                    val views = RemoteViews(context.packageName, R.layout.widget_weather).apply {
                        val cached = prefs.getString("weather_cached_result", null)
                        if (cached != null) {
                            val parts = cached.split("|")
                            setTextViewText(R.id.weather_city, (parts.getOrElse(3) { city }).uppercase())
                            setTextViewText(R.id.weather_temp, "${parts.getOrElse(0) { "--°" }}")
                            setTextViewText(R.id.weather_details, "Offline • Humidity ${parts.getOrElse(1) { "--" }}% • Wind ${parts.getOrElse(2) { "--" }} km/h")
                        } else {
                            setTextViewText(R.id.weather_details, "Weather unavailable • open app to retry")
                        }
                        WidgetThemeSupport.apply(context, this, R.id.widget_root, R.id.weather_city, R.id.weather_temp, R.id.weather_details)
                    }
                    manager.updateAppWidget(id, views)
                }
            } finally { pending.finish() }
        }.start()
    }
}
