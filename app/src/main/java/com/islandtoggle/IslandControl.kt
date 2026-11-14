package com.islandtoggle

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

object IslandControl {
    const val TARGET_PKG = "com.droidbubble.app"
    private const val DEFAULT_SERVICE = "private const val DEFAULT_SERVICE = "$TARGET_PKG/$TARGET_PKG.service.IslandAccessibilityService""
    private const val PREFS = "island"
    private const val KEY_LAST = "last_component"

    fun canWrite(ctx: Context) =
        ctx.checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") ==
            PackageManager.PERMISSION_GRANTED

    private fun enabledList(ctx: Context): List<String> =
        (Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: "")
            .split(":").filter { it.isNotBlank() }

    private fun isTarget(entry: String) = entry.startsWith("$TARGET_PKG/")

    fun isOn(ctx: Context) = enabledList(ctx).any { isTarget(it) }

    /** Turns the island's accessibility service on/off. Needs WRITE_SECURE_SETTINGS. */
    fun set(ctx: Context, on: Boolean) {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val list = enabledList(ctx).toMutableList()
        if (on) {
            if (list.none { isTarget(it) }) {
                list.add(prefs.getString(KEY_LAST, DEFAULT_SERVICE) ?: DEFAULT_SERVICE)
            }
        } else {
            list.filter { isTarget(it) }.firstOrNull()?.let {
                prefs.edit().putString(KEY_LAST, it).apply()
            }
            list.removeAll { isTarget(it) }
        }
        val s = ctx.contentResolver
        Settings.Secure.putString(s, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, list.joinToString(":"))
        Settings.Secure.putString(s, Settings.Secure.ACCESSIBILITY_ENABLED, if (list.isEmpty()) "0" else "1")
    }
}
