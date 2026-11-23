package com.glasswidgetsx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import org.json.JSONObject

class SettingsActivity : Activity() {
    private val backupRequest = 7101
    private val restoreRequest = 7102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(8, 9, 13))
            isFillViewport = true
        }
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(30))
        }
        scroll.addView(page)
        setContentView(scroll)

        page.addView(title("SETTINGS"))
        page.addView(title("Preferences"))
        val themeMode = Spinner(this).apply {
            adapter = ArrayAdapter(this@SettingsActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("Dark", "Light", "System"))
            val mode = getSharedPreferences("glass_studio", MODE_PRIVATE).getString("app_theme", "Dark")
            setSelection(listOf("Dark", "Light", "System").indexOf(mode).coerceAtLeast(0))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    getSharedPreferences("glass_studio", MODE_PRIVATE).edit()
                        .putString("app_theme", parent?.getItemAtPosition(position).toString()).apply()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        page.addView(panel("Appearance", themeMode))

        val language = Spinner(this).apply {
            adapter = ArrayAdapter(this@SettingsActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("English", "Hindi"))
            val value = getSharedPreferences("glass_studio", MODE_PRIVATE).getString("language", "English")
            setSelection(listOf("English", "Hindi").indexOf(value).coerceAtLeast(0))
        }
        language.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                getSharedPreferences("glass_studio", MODE_PRIVATE).edit()
                    .putString("language", parent?.getItemAtPosition(position).toString()).apply()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        page.addView(panel("Language", language))

        page.addView(title("Clock"))
        val clock24 = Switch(this).apply {
            text = "24-hour format"
            setTextColor(Color.WHITE)
            val prefs = getSharedPreferences("glass_studio", MODE_PRIVATE)
            isChecked = prefs.getBoolean("clock_24_hour", android.text.format.DateFormat.is24HourFormat(this@SettingsActivity))
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("clock_24_hour", checked).apply()
                com.glasswidgetsx.widgets.WidgetThemeSupport.refreshAll(this@SettingsActivity)
            }
        }
        val seconds = Switch(this).apply {
            text = "Show seconds"
            setTextColor(Color.WHITE)
            val prefs = getSharedPreferences("glass_studio", MODE_PRIVATE)
            isChecked = prefs.getBoolean("clock_show_seconds", false)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("clock_show_seconds", checked).apply()
                com.glasswidgetsx.widgets.WidgetThemeSupport.refreshAll(this@SettingsActivity)
            }
        }
        page.addView(panel("Clock options", clock24, seconds))

        page.addView(title("Backup"))
        page.addView(button("CREATE LOCAL BACKUP") { chooseBackupLocation() })
        page.addView(button("RESTORE LOCAL BACKUP") { chooseRestoreFile() })
        page.addView(button("RESET ALL SETTINGS") {
            getSharedPreferences("glass_studio", MODE_PRIVATE).edit().clear().apply()
            ThemeManager.reset(this)
            Toast.makeText(this, "All app settings have been reset.", Toast.LENGTH_LONG).show()
        })

        page.addView(title("Permissions"))
        page.addView(button("NOTIFICATION SETTINGS") {
            val intent = if (android.os.Build.VERSION.SDK_INT >= 26)
                Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
            else Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
            startActivity(intent)
        })
        page.addView(button("APP PERMISSIONS") {
            startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName")))
        })
        page.addView(title("Help & privacy"))
        page.addView(text("Weather data is provided by Open-Meteo. Local backups stay on your device unless you choose a cloud-backed location. Icon packs and live wallpapers follow launcher and system restrictions."))
        page.addView(button("OPEN ANDROID HELP") {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://support.google.com/android")))
        })
    }

    private fun chooseBackupLocation() {
        startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, "glass_widgets_x_backup.json")
        }, backupRequest)
    }

    private fun chooseRestoreFile() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }, restoreRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            if (requestCode == backupRequest) {
                val root = JSONObject()
                fileList().forEach { name ->
                    val prefs = getSharedPreferences(name.removeSuffix(".xml"), MODE_PRIVATE)
                    val values = JSONObject()
                    prefs.all.forEach { (key, value) ->
                        when (value) {
                            is Set<*> -> values.put(key, org.json.JSONArray(value.map { it.toString() }))
                            else -> values.put(key, value)
                        }
                    }
                    root.put(name.removeSuffix(".xml"), values)
                }
                contentResolver.openOutputStream(uri, "wt")?.bufferedWriter().use {
                    it?.write(root.toString(2))
                }
                Toast.makeText(this, "Backup created.", Toast.LENGTH_LONG).show()
            } else if (requestCode == restoreRequest) {
                val raw = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
                val root = JSONObject(raw ?: "{}")
                val names = root.keys()
                while (names.hasNext()) {
                    val name = names.next()
                    val values = root.optJSONObject(name) ?: continue
                    val editor = getSharedPreferences(name, MODE_PRIVATE).edit()
                    val keys = values.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        val value = values.get(key)
                        when (value) {
                            is Boolean -> editor.putBoolean(key, value)
                            is Int -> editor.putInt(key, value)
                            is Long -> editor.putLong(key, value)
                            is Float -> editor.putFloat(key, value)
                            is String -> editor.putString(key, value)
                            is org.json.JSONArray -> editor.putStringSet(key,
                                (0 until value.length()).map { value.optString(it) }.toSet())
                        }
                    }
                    editor.apply()
                }
                com.glasswidgetsx.widgets.WidgetThemeSupport.refreshAll(this)
                Toast.makeText(this, "Backup restored.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Backup operation failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun title(value: String) = TextView(this).apply {
        text = value
        textSize = 12f
        setTextColor(Color.rgb(216, 197, 255))
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        setPadding(0, dp(20), 0, dp(8))
    }
    private fun text(value: String) = TextView(this).apply {
        text = value
        setTextColor(0xFFD5D0DF.toInt())
        textSize = 13f
        setPadding(0, dp(10), 0, dp(10))
    }
    private fun panel(label: String, vararg views: android.view.View) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(16), dp(16), dp(16), dp(16))
        background = GradientDrawable().apply {
            cornerRadius = dp(18).toFloat()
            setColor(0x22FFFFFF)
            setStroke(dp(1), 0x55FFFFFF)
        }
        addView(TextView(context).apply {
            text = label
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, dp(8))
        })
        views.forEach { addView(it, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(4) }) }
    }
    private fun button(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        isAllCaps = false
        setTextColor(0xFF17131F.toInt())
        background = GradientDrawable().apply {
            cornerRadius = dp(14).toFloat()
            setColor(Color.rgb(216, 197, 255))
        }
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(8) }
    }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
}
