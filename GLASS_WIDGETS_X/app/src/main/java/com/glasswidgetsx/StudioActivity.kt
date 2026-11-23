package com.glasswidgetsx

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.provider.CalendarContract
import android.provider.MediaStore
import android.view.Gravity
import android.widget.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import com.glasswidgetsx.ThemeManager
import com.glasswidgetsx.WidgetTheme
import com.glasswidgetsx.widgets.WidgetThemeSupport

class StudioActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("glass_studio", MODE_PRIVATE) }
    private val lavender = Color.rgb(216, 197, 255)
    private lateinit var preview: TextView
    private lateinit var weatherResult: TextView
    private var selectedPhoto: Uri? = null
    private val pickPhoto = 501

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 9, 13)
        window.navigationBarColor = Color.rgb(8, 9, 13)
        val scroll = ScrollView(this).apply { setBackgroundColor(Color.rgb(8,9,13)) }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(30))
        }
        scroll.addView(page)
        setContentView(scroll)

        page.addView(label("GLASS WIDGETS X  /  STUDIO", 12, lavender, true))
        page.addView(label("Make it yours.", 30, Color.WHITE, true).apply {
            gravity = Gravity.START
            setPadding(0, dp(10), 0, dp(6))
        })
        page.addView(label("Choose a style and preview your saved preferences.", 13, 0xFFBDB8C8.toInt(), false).apply {
            gravity = Gravity.START
        })

        heading(page, "01  WIDGET DESIGN STUDIO")
        val design = panel()
        preview = label("10:28\nSUNDAY  •  GLASS", 26, Color.WHITE, true)
        preview.gravity = Gravity.CENTER
        preview.background = glassBackground(prefs.getInt("accent", 0xFFD8C5FF.toInt()), prefs.getInt("opacity", 22))
        design.addView(preview, LinearLayout.LayoutParams(-1, dp(116)))
        design.addView(label("Clock style", 13, lavender, true).apply { setPadding(0, dp(14), 0, dp(6)) })
        val styles = listOf(
            "Minimal", "Bold", "Mono", "Elegant", "Compact", "Aurora", "Midnight",
            "Ocean", "Rose", "Forest", "Amber", "Frost", "Neon", "Solar",
            "Soft", "Chrome", "Arcade", "Vintage", "Zen", "Nova", "Carbon",
            "Lilac", "Ember", "Prism"
        )
        val styleRow = GridLayout(this).apply {
            columnCount = 4
        }
        styles.forEach { style ->
            styleRow.addView(smallButton(style) {
                prefs.edit().putString("clock_style", style).apply()
                preview.text = if (style == "Compact") "10:28  •  SUN" else "10:28\nSUNDAY  •  $style"
                preview.textSize = prefs.getInt("text_size", 34).toFloat()
                Toast.makeText(this, "$style preview saved", Toast.LENGTH_SHORT).show()
            }.apply {
                textSize = 9f
            }, GridLayout.LayoutParams().apply {
                width = 0
                height = dp(38)
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dp(3), dp(3), dp(3), dp(3))
            })
        }
        design.addView(styleRow)
        design.addView(label("Glass transparency", 13, lavender, true).apply { setPadding(0, dp(12), 0, dp(4)) })
        val opacity = SeekBar(this).apply {
            max = 70
            progress = prefs.getInt("opacity", 22)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                    prefs.edit().putInt("opacity", value).apply()
                    preview.background = glassBackground(prefs.getInt("accent", 0xFFD8C5FF.toInt()), value)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        design.addView(opacity)
        design.addView(label("Font size", 13, lavender, true).apply { setPadding(0, dp(12), 0, dp(4)) })
        val fontSize = SeekBar(this).apply {
            min = 20
            max = 48
            progress = prefs.getInt("text_size", 34)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                    prefs.edit().putInt("text_size", value).apply()
                    preview.textSize = value.toFloat()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        design.addView(fontSize)
        design.addView(label("Corner radius", 13, lavender, true).apply { setPadding(0, dp(12), 0, dp(4)) })
        val radius = SeekBar(this).apply {
            max = 36
            progress = prefs.getInt("radius", 22)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, value: Int, fromUser: Boolean) {
                    prefs.edit().putInt("radius", value).apply()
                    preview.background = glassBackground(prefs.getInt("accent", 0xFFD8C5FF.toInt()), opacity.progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        design.addView(radius)
        val clock24 = CheckBox(this).apply {
            text = "Use 24-hour clock"
            setTextColor(Color.WHITE)
            isChecked = prefs.getBoolean("clock_24_hour", false)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("clock_24_hour", checked).apply()
                WidgetThemeSupport.refreshAll(this@StudioActivity)
            }
        }
        val clockSeconds = CheckBox(this).apply {
            text = "Show seconds"
            setTextColor(Color.WHITE)
            isChecked = prefs.getBoolean("clock_show_seconds", false)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("clock_show_seconds", checked).apply()
                WidgetThemeSupport.refreshAll(this@StudioActivity)
            }
        }
        design.addView(clock24)
        design.addView(clockSeconds)
        design.addView(smallButton("Change accent color") {
            val colors = intArrayOf(0xFFD8C5FF.toInt(), 0xFF9DE7D7.toInt(), 0xFFFFC7A8.toInt(), 0xFF9FC8FF.toInt())
            val current = prefs.getInt("color_index", 0)
            val next = (current + 1) % colors.size
            prefs.edit().putInt("color_index", next).putInt("accent", colors[next]).apply()
            preview.background = glassBackground(colors[next], opacity.progress)
        })
        val themeName = EditText(this).apply {
            hint = "Theme name"
            setHintTextColor(0xFF888393.toInt())
            setTextColor(Color.WHITE)
            setSingleLine(true)
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = glassBackground(0xFFFFFFFF.toInt(), 12)
        }
        design.addView(themeName, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(10) })
        val actionRow = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        actionRow.addView(smallButton("SAVE THEME") {
            val name = themeName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a theme name.", Toast.LENGTH_SHORT).show()
            } else {
                ThemeManager.save(this, name, ThemeManager.get(this).copy(name = name))
                Toast.makeText(this, "Theme saved: $name", Toast.LENGTH_SHORT).show()
            }
        })
        actionRow.addView(smallButton("LOAD SAVED THEME") {
            val name = themeName.text.toString().trim()
            if (name.isEmpty()) {
                val names = ThemeManager.savedNames(this)
                if (names.isEmpty()) Toast.makeText(this, "No saved themes.", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Enter one of: ${names.joinToString()}", Toast.LENGTH_LONG).show()
            } else {
                val theme = ThemeManager.load(this, name)
                if (theme == null) Toast.makeText(this, "Theme not found.", Toast.LENGTH_SHORT).show()
                else {
                    preview.background = glassBackground(theme.accent, theme.opacity)
                    preview.textSize = theme.textSize.toFloat()
                    opacity.progress = theme.opacity
                    radius.progress = theme.radius
                    fontSize.progress = theme.textSize
                    Toast.makeText(this, "Loaded: $name", Toast.LENGTH_SHORT).show()
                }
            }
        })
        actionRow.addView(smallButton("APPLY TO ALL WIDGETS") {
            val current = ThemeManager.get(this)
            ThemeManager.save(this, current.name, current)
            WidgetThemeSupport.refreshAll(this)
            Toast.makeText(this, "Theme applied to existing widgets.", Toast.LENGTH_SHORT).show()
        })
        actionRow.addView(smallButton("RESET DESIGN") {
            ThemeManager.reset(this)
            opacity.progress = 24
            radius.progress = 22
            fontSize.progress = 34
            preview.text = "10:28\nSUNDAY  •  GLASS"
            preview.textSize = 34f
            preview.background = glassBackground(0xFFD8C5FF.toInt(), 24)
            Toast.makeText(this, "Design reset.", Toast.LENGTH_SHORT).show()
        })
        design.addView(actionRow)
        design.addView(label("Themes and widget colors are persisted on this device and applied through Android AppWidgetManager.", 11, 0xFFAAA5B5.toInt(), false).apply {
            setPadding(0, dp(10), 0, 0)
        })
        page.addView(design)

        heading(page, "02  WEATHER")
        val weather = panel()
        val city = EditText(this).apply {
            hint = "Enter city (e.g. Patna)"
            setHintTextColor(0xFF888393.toInt())
            setTextColor(Color.WHITE)
            setSingleLine(true)
            background = glassBackground(0xFFFFFFFF.toInt(), 12)
            setPadding(dp(12), 0, dp(12), 0)
        }
        weather.addView(city, LinearLayout.LayoutParams(-1, dp(48)))
        weatherResult = label("Weather result will appear here.", 13, Color.WHITE, false)
        weather.addView(smallButton("GET WEATHER") { fetchWeather(city.text.toString().trim()) })
        weather.addView(smallButton("SAVE CITY FOR HOME WIDGET") {
            val value = city.text.toString().trim()
            if (value.isNotEmpty()) {
                prefs.edit().putString("weather_city", value).apply()
                Toast.makeText(this, "Weather widget city saved.", Toast.LENGTH_SHORT).show()
            }
        })
        weather.addView(weatherResult)
        page.addView(weather)

        heading(page, "03  PHOTO FRAME")
        val photo = panel()
        photo.addView(label("Choose a photo from your device, then preview it here.", 13, Color.WHITE, false))
        photo.addView(smallButton("PICK PHOTO") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, pickPhoto)
        })
        photo.addView(smallButton("SET AS DEVICE WALLPAPER") { setPickedWallpaper() })
        page.addView(photo)

        heading(page, "04  WALLPAPERS & THEMES")
        val wall = panel()
        wall.addView(label("Use a selected image as your Android wallpaper. Theme accent and opacity preferences are saved locally.", 13, Color.WHITE, false))
        wall.addView(smallButton("OPEN WALLPAPER SETTINGS") {
            startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        })
        page.addView(wall)

        heading(page, "05  MUSIC")
        val music = panel()
        music.addView(label("Open the music app installed on this phone. Playback controls inside a home-screen widget need a separate media-session integration.", 13, Color.WHITE, false))
        music.addView(smallButton("OPEN MUSIC APP") {
            val launch = packageManager.getLaunchIntentForPackage("com.google.android.music")
                ?: packageManager.getLaunchIntentForPackage("com.spotify.music")
                ?: Intent("android.intent.action.MUSIC_PLAYER")
            try { startActivity(launch) } catch (_: Exception) {
                Toast.makeText(this, "No supported music app found.", Toast.LENGTH_SHORT).show()
            }
        })
        page.addView(music)

        heading(page, "06  CALENDAR & SMART TOOLS")
        val calendar = panel()
        calendar.addView(label("Create a calendar event using the calendar app on your device.", 13, Color.WHITE, false))
        calendar.addView(smallButton("GRANT CALENDAR ACCESS") {
            if (android.os.Build.VERSION.SDK_INT >= 23 &&
                checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_CALENDAR), 82)
            } else {
                Toast.makeText(this, "Calendar access already granted.", Toast.LENGTH_SHORT).show()
            }
        })
        calendar.addView(smallButton("CREATE CALENDAR EVENT") {
            val intent = Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "New event")
            try { startActivity(intent) } catch (_: Exception) {
                Toast.makeText(this, "No calendar app available.", Toast.LENGTH_SHORT).show()
            }
        })
        page.addView(calendar)

        heading(page, "07  ICON COLLECTIONS")
        val icons = panel()
        icons.addView(label("Icon collection preview and launcher-wide icon replacement are different capabilities. This build provides curated style choices; applying icons across the phone requires launcher support or a compatible icon-pack integration.", 12, 0xFFD5D0DF.toInt(), false))
        icons.addView(smallButton("CYCLE ICON STYLE") {
            val options = listOf("Glass outline", "Soft monochrome", "Lavender gradient", "Minimal line")
            val next = (prefs.getInt("icon_style", -1) + 1) % options.size
            prefs.edit().putInt("icon_style", next).apply()
            Toast.makeText(this, "Selected: ${options[next]}", Toast.LENGTH_SHORT).show()
        })
        page.addView(icons)

        heading(page, "08  APP SHORTCUTS")
        val shortcuts = panel()
        shortcuts.addView(label("Open common Android settings from here.", 13, Color.WHITE, false))
        shortcuts.addView(smallButton("OPEN DISPLAY SETTINGS") { openSettings(android.provider.Settings.ACTION_DISPLAY_SETTINGS) })
        shortcuts.addView(smallButton("OPEN SOUND SETTINGS") { openSettings(android.provider.Settings.ACTION_SOUND_SETTINGS) })
        shortcuts.addView(smallButton("OPEN BLUETOOTH SETTINGS") { openSettings(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS) })
        page.addView(shortcuts)

        heading(page, "09  BACKGROUND TIMER")
        val timer = panel()
        timer.addView(label("The stopwatch and countdown tools are available in Tools. Start the foreground countdown from the timer screen for ongoing Android notifications.", 12, 0xFFD5D0DF.toInt(), false))
        timer.addView(smallButton("OPEN STOPWATCH & TIMER") { startActivity(Intent(this, ToolsActivity::class.java)) })
        page.addView(timer)

        page.addView(label("GLASS WIDGETS X  •  PREMIUM STUDIO", 10, 0xFF8C8797.toInt(), true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(24), 0, 0)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickPhoto && resultCode == RESULT_OK) {
            selectedPhoto = data?.data
            selectedPhoto?.let {
                try {
                    contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: Exception) {}
                Toast.makeText(this, "Photo selected. You can set it as wallpaper.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 82) {
            val granted = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            Toast.makeText(
                this,
                if (granted) "Calendar access granted." else "Calendar permission was denied.",
                Toast.LENGTH_SHORT
            ).show()
            com.glasswidgetsx.widgets.WidgetThemeSupport.refreshAll(this)
        }
    }

    private fun setPickedWallpaper() {
        val uri = selectedPhoto
        if (uri == null) {
            Toast.makeText(this, "Pick a photo first.", Toast.LENGTH_SHORT).show()
            return
        }
        Thread {
            try {
                val bitmap = contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }
                if (bitmap != null) {
                    WallpaperManager.getInstance(this).setBitmap(bitmap)
                    runOnUiThread { Toast.makeText(this, "Wallpaper applied.", Toast.LENGTH_LONG).show() }
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this, "Could not apply wallpaper: ${e.message}", Toast.LENGTH_LONG).show() }
            }
        }.start()
    }

    private fun fetchWeather(city: String) {
        if (city.isBlank()) {
            weatherResult.text = "Enter a city name first."
            return
        }
        weatherResult.text = "Loading weather for $city…"
        Thread {
            try {
                val geo = URL("https://geocoding-api.open-meteo.com/v1/search?name=${java.net.URLEncoder.encode(city, "UTF-8")}&count=1&language=en&format=json")
                val geoConn = geo.openConnection() as HttpURLConnection
                geoConn.connectTimeout = 10000
                geoConn.readTimeout = 10000
                val geoJson = JSONObject(geoConn.inputStream.bufferedReader().use { it.readText() })
                val result = geoJson.optJSONArray("results")?.optJSONObject(0)
                    ?: throw IllegalStateException("City not found")
                val lat = result.getDouble("latitude")
                val lon = result.getDouble("longitude")
                val place = result.optString("name", city)
                val forecastUrl = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m")
                val fConn = forecastUrl.openConnection() as HttpURLConnection
                fConn.connectTimeout = 10000
                fConn.readTimeout = 10000
                val forecast = JSONObject(fConn.inputStream.bufferedReader().use { it.readText() })
                val current = forecast.getJSONObject("current")
                val resultText = "$place\\n${current.optDouble("temperature_2m")}°C  •  Feels ${current.optDouble("apparent_temperature")}°C\\nHumidity ${current.optInt("relative_humidity_2m")}%  •  Wind ${current.optDouble("wind_speed_10m")} km/h"
                runOnUiThread { weatherResult.text = resultText }
            } catch (e: Exception) {
                runOnUiThread { weatherResult.text = "Weather unavailable: ${e.message ?: "check internet"}" }
            }
        }.start()
    }

    private fun openSettings(action: String) {
        try { startActivity(Intent(action)) } catch (_: Exception) {
            Toast.makeText(this, "Setting is not available on this device.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun heading(parent: LinearLayout, value: String) {
        parent.addView(label(value, 12, lavender, true).apply {
            gravity = Gravity.START
            setPadding(0, dp(24), 0, dp(10))
            letterSpacing = 0.08f
        })
    }

    private fun panel() = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = glassBackground(0xFFFFFFFF.toInt(), 18)
    }

    private fun glassBackground(color: Int, opacity: Int) = GradientDrawable().apply {
        cornerRadius = dp(22).toFloat()
        val alpha = (opacity.coerceIn(5, 75) * 255 / 100) shl 24
        setColor((color and 0x00FFFFFF) or alpha)
        setStroke(dp(1), 0x55FFFFFF)
    }

    private fun label(value: String, size: Int, color: Int, bold: Boolean) =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

    private fun smallButton(value: String, parent: LinearLayout, action: () -> Unit) {
        parent.addView(smallButton(value, action), LinearLayout.LayoutParams(-1, dp(44)).apply {
            topMargin = dp(10)
        })
    }

    private fun smallButton(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        isAllCaps = false
        textSize = 11f
        setTextColor(0xFF17131F.toInt())
        background = GradientDrawable().apply {
            cornerRadius = dp(14).toFloat()
            setColor(lavender)
        }
        setOnClickListener { action() }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
