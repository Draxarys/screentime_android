package com.aarav.screentimer

import android.content.Context
import java.time.LocalDate

/** Saves screen time per day (in milliseconds) on the phone. Nothing leaves the device. */
object TimeStore {
    private const val PREFS = "screen_time"
    private const val KEY_ENABLED = "overlay_enabled"
    private const val KEY_X = "overlay_x"
    private const val KEY_Y = "overlay_y"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private fun key(date: LocalDate) = "total_$date"

    fun get(ctx: Context, date: LocalDate = LocalDate.now()): Long =
        prefs(ctx).getLong(key(date), 0L)

    fun add(ctx: Context, millis: Long) {
        if (millis <= 0) return
        val k = key(LocalDate.now())
        val p = prefs(ctx)
        p.edit().putLong(k, p.getLong(k, 0L) + millis).apply()
    }

    fun isEnabled(ctx: Context) = prefs(ctx).getBoolean(KEY_ENABLED, false)
    fun setEnabled(ctx: Context, on: Boolean) = prefs(ctx).edit().putBoolean(KEY_ENABLED, on).apply()

    fun position(ctx: Context): Pair<Int, Int> =
        prefs(ctx).getInt(KEY_X, 24) to prefs(ctx).getInt(KEY_Y, 120)

    fun savePosition(ctx: Context, x: Int, y: Int) =
        prefs(ctx).edit().putInt(KEY_X, x).putInt(KEY_Y, y).apply()

    fun format(millis: Long): String {
        val s = millis / 1000
        return "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
    }
}
