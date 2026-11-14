package com.islandtoggle

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }
        fun text(s: String, size: Float = 16f) = TextView(this).apply {
            this.text = s; textSize = size; setPadding(0, 0, 0, pad / 2)
        }
        col.addView(text("Island Toggle", 26f))
        status = text("", 16f); col.addView(status)

        col.addView(text(
            "1. Pull down Quick Settings, tap the pencil/edit icon, and drag the \"Dynamic Island\" tile in.\n\n" +
            "2. For a real one-tap toggle, grant permission once using Wireless Debugging (LADB or Shizuku shell):\n\n" +
            "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS\n\n" +
            "Without it, the tile just opens Accessibility settings."
        ))
        col.addView(Button(this).apply {
            text = "Copy grant command"
            setOnClickListener {
                (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    ClipData.newPlainText("cmd", "pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"))
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }
        })
        col.addView(Button(this).apply {
            text = "Open Accessibility settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        })
        setContentView(ScrollView(this).apply { addView(col) })
    }

    override fun onResume() {
        super.onResume()
        status.text = "Permission: " + (if (IslandControl.canWrite(this)) "granted ✔" else "not granted") +
            "\nDynamic Island: " + (if (IslandControl.isOn(this)) "on" else "off")
    }
}
