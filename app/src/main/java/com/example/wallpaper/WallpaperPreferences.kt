package com.example.wallpaper

import android.content.Context
import android.content.SharedPreferences

class WallpaperPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("naruto_wallpaper_prefs", Context.MODE_PRIVATE)

    var selectedCharacterId: String
        get() = prefs.getString(KEY_CHARACTER_ID, "naruto_sixpaths") ?: "naruto_sixpaths"
        set(value) = prefs.edit().putString(KEY_CHARACTER_ID, value).apply()

    var particleDensity: Int
        get() = prefs.getInt(KEY_PARTICLE_DENSITY, 2) // 1: Low, 2: Med, 3: Ultra
        set(value) = prefs.edit().putInt(KEY_PARTICLE_DENSITY, value).apply()

    var atmosphereEffect: String
        get() = prefs.getString(KEY_ATMOSPHERE, "JUTSU_ENERGY") ?: "JUTSU_ENERGY"
        set(value) = prefs.edit().putString(KEY_ATMOSPHERE, value).apply()

    var colorFilter: String
        get() = prefs.getString(KEY_COLOR_FILTER, "VIBRANT") ?: "VIBRANT"
        set(value) = prefs.edit().putString(KEY_COLOR_FILTER, value).apply()

    var clockStyle: String
        get() = prefs.getString(KEY_CLOCK_STYLE, "NEON_NINJA") ?: "NEON_NINJA"
        set(value) = prefs.edit().putString(KEY_CLOCK_STYLE, value).apply()

    var showBatteryWidget: Boolean
        get() = prefs.getBoolean(KEY_SHOW_BATTERY_WIDGET, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_BATTERY_WIDGET, value).apply()

    var showQuote: Boolean
        get() = prefs.getBoolean(KEY_SHOW_QUOTE, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_QUOTE, value).apply()

    var soundOnTouch: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ON_TOUCH, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ON_TOUCH, value).apply()

    var soundOnCharging: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ON_CHARGING, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ON_CHARGING, value).apply()

    var showNinjaHud: Boolean
        get() = prefs.getBoolean(KEY_SHOW_NINJA_HUD, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_NINJA_HUD, value).apply()

    var parallaxEnabled: Boolean
        get() = prefs.getBoolean(KEY_PARALLAX, true)
        set(value) = prefs.edit().putBoolean(KEY_PARALLAX, value).apply()

    var chargingStyle: String
        get() = prefs.getString(KEY_CHARGING_STYLE, "CHAKRA_SEAL") ?: "CHAKRA_SEAL"
        set(value) = prefs.edit().putString(KEY_CHARGING_STYLE, value).apply()

    companion object {
        private const val KEY_CHARACTER_ID = "selected_character_id"
        private const val KEY_PARTICLE_DENSITY = "particle_density"
        private const val KEY_ATMOSPHERE = "atmosphere_effect"
        private const val KEY_COLOR_FILTER = "color_filter"
        private const val KEY_CLOCK_STYLE = "clock_style"
        private const val KEY_SHOW_BATTERY_WIDGET = "show_battery_widget"
        private const val KEY_SHOW_QUOTE = "show_quote"
        private const val KEY_SOUND_ON_TOUCH = "sound_on_touch"
        private const val KEY_SOUND_ON_CHARGING = "sound_on_charging"
        private const val KEY_SHOW_NINJA_HUD = "show_ninja_hud"
        private const val KEY_PARALLAX = "parallax_enabled"
        private const val KEY_CHARGING_STYLE = "charging_style"
    }
}
