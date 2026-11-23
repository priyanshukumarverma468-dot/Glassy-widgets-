package com.glasswidgetsx

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import com.glasswidgetsx.widgets.BatteryWidgetProvider
import com.glasswidgetsx.widgets.ClockWidgetProvider
import com.glasswidgetsx.widgets.DateWidgetProvider
import com.glasswidgetsx.widgets.AnalogClockWidgetProvider
import com.glasswidgetsx.widgets.NoteWidgetProvider

class MainActivity : Activity() {
    private val lavender = Color.rgb(216, 197, 255)
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8, 9, 13)
        window.navigationBarColor = Color.rgb(8, 9, 13)

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(8, 9, 13))
            isFillViewport = true
        }
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(26), dp(20), dp(28))
        }
        scroll.addView(content)
        setContentView(scroll)

        text("GLASS WIDGETS X", 13f, lavender, true).also {
            it.letterSpacing = 0.18f
            content.addView(it)
        }
        text("Make your home screen\nfeel different.", 30f, Color.WHITE, true).also {
            it.setPadding(0, dp(12), 0, dp(8))
            content.addView(it)
        }
        text("A glass-style widget studio for Android.", 14f, 0xFFB9B5C7.toInt(), false).also {
            content.addView(it)
        }
        val hero = card()
        hero.addView(text("YOUR WIDGET STUDIO", 11f, lavender, true))
        hero.addView(text("Choose a widget, then add it to your home screen.", 17f, Color.WHITE, true).apply {
            setPadding(0, dp(10), 0, dp(14))
        })
        hero.addView(text("Widgets are real Android home-screen widgets. Your launcher controls final placement and resizing.", 13f, 0xFFD2CFDA.toInt(), false))
        content.addView(hero, marginTop(24))

        section("CORE WIDGETS", "Ready to add")
        widgetCard("Digital Glass Clock", "Live time with date • Resizable", "ADD CLOCK") {
            addWidget(ClockWidgetProvider::class.java)
        }
        widgetCard("Battery Glass Card", "Real battery level and charging state", "ADD BATTERY") {
            addWidget(BatteryWidgetProvider::class.java)
        }
        widgetCard("Weather Glass Widget", "Current temperature, humidity and wind", "ADD WEATHER") {
            addWidget(com.glasswidgetsx.widgets.WeatherWidgetProvider::class.java)
        }
        widgetCard("Calendar Agenda Widget", "Next upcoming calendar event", "ADD CALENDAR") {
            addWidget(com.glasswidgetsx.widgets.CalendarWidgetProvider::class.java)
        }
        widgetCard("Photo Frame Widget", "Choose a photo during widget setup", "ADD PHOTO") {
            addWidget(com.glasswidgetsx.widgets.PhotoWidgetProvider::class.java)
        }
        widgetCard("Date & Calendar", "Large date, month and weekday", "ADD DATE") {
            addWidget(DateWidgetProvider::class.java)
        }
        widgetCard("Analog Glass Clock", "Native analog clock face", "ADD ANALOG CLOCK") {
            addWidget(AnalogClockWidgetProvider::class.java)
        }
        widgetCard("Quick Notes", "Write and save a note on your home screen", "ADD NOTE") {
            addWidget(NoteWidgetProvider::class.java)
        }
        widgetCard("Search Widget", "Open Android web search from the home screen", "ADD SEARCH") {
            addWidget(com.glasswidgetsx.widgets.SearchWidgetProvider::class.java)
        }
        widgetCard("Quick Launch", "Camera, gallery, app search and settings", "ADD SHORTCUTS") {
            addWidget(com.glasswidgetsx.widgets.QuickLaunchWidgetProvider::class.java)
        }

        section("CUSTOMIZATION & SMART FEATURES", "Open feature studio")
        widgetCard("Glass Widget Studio", "Themes, clock styles, photo, wallpaper and smart tools", "OPEN STUDIO") {
            startActivity(Intent(this, StudioActivity::class.java))
        }

        section("UTILITY TOOLS", "Working in-app tools")
        widgetCard("Stopwatch & Countdown Timer", "Start, pause, reset and run a countdown", "OPEN TOOLS") {
            startActivity(Intent(this, ToolsActivity::class.java))
        }

        content.addView(text("BUILD 1.4.0  •  ANDROID 8.0+", 10f, 0xFF777382.toInt(), true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(26), 0, 0)
            letterSpacing = 0.12f
        })
    }

    private fun section(title: String, subtitle: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(26), 0, dp(10))
        }
        val left = text(title, 12f, Color.WHITE, true)
        left.letterSpacing = 0.12f
        row.addView(left, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(text(subtitle, 11f, 0xFF9691A2.toInt(), false))
        content.addView(row)
    }

    private fun widgetCard(title: String, detail: String, action: String, onClick: () -> Unit) {
        val box = card()
        box.addView(text(title, 18f, Color.WHITE, true))
        box.addView(text(detail, 13f, 0xFFC3BFCD.toInt(), false).apply {
            setPadding(0, dp(5), 0, dp(14))
        })
        val button = Button(this).apply {
            text = action
            isAllCaps = false
            textSize = 12f
            setTextColor(Color.rgb(22, 18, 31))
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                cornerRadius = dp(14).toFloat()
                setColor(lavender)
            }
            setOnClickListener { onClick() }
        }
        box.addView(button, LinearLayout.LayoutParams(-1, dp(46)))
        content.addView(box, marginTop(10))
    }

    private fun addWidget(provider: Class<*>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            AppWidgetManager.getInstance(this).isRequestPinAppWidgetSupported) {
            val component = ComponentName(this, provider)
            AppWidgetManager.getInstance(this).requestPinAppWidget(component, null, null)
            Toast.makeText(this, "Confirm the widget pin request on your home screen.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Open your home screen, long-press an empty area, choose Widgets, then find Glass Widgets X.", Toast.LENGTH_LONG).show()
        }
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(18), dp(18), dp(18))
        background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(0x3AFFFFFF, 0x16FFFFFF)
        ).apply {
            cornerRadius = dp(24).toFloat()
            setStroke(dp(1), 0x55FFFFFF)
        }
    }

    private fun text(value: String, size: Float, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) typeface = Typeface.create("sans-serif", Typeface.BOLD)
        }

    private fun marginTop(top: Int) = LinearLayout.LayoutParams(-1, -2).apply {
        topMargin = top
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
