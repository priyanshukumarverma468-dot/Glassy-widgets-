package com.glasswidgetsx.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class NoteConfigActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 28)
            setBackgroundColor(Color.rgb(10, 10, 16))
        }
        root.addView(TextView(this).apply {
            text = "EDIT YOUR NOTE"
            textSize = 18f
            setTextColor(Color.WHITE)
        })
        val input = EditText(this).apply {
            hint = "Write something..."
            setTextColor(Color.WHITE)
            setHintTextColor(0xFFAAA5B5.toInt())
            minLines = 4
            gravity = Gravity.TOP
            setPadding(16, 16, 16, 16)
            background = GradientDrawable().apply {
                cornerRadius = 18f
                setColor(0x22FFFFFF)
                setStroke(1, 0x66FFFFFF)
            }
        }
        root.addView(input, LinearLayout.LayoutParams(-1, 0, 1f).apply { topMargin = 24; bottomMargin = 20 })
        root.addView(Button(this).apply {
            text = "SAVE NOTE"
            setOnClickListener {
                val value = input.text.toString().trim().ifEmpty { "Tap to write your note" }
                getSharedPreferences("glass_note_widget", MODE_PRIVATE).edit().putString("note_$widgetId", value).apply()
                NoteWidgetProvider.update(this@NoteConfigActivity, AppWidgetManager.getInstance(this@NoteConfigActivity), widgetId)
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                finish()
            }
        }, LinearLayout.LayoutParams(-1, 54))
        setContentView(root)
    }
}
