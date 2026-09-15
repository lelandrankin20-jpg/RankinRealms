package com.rankinenvironments.realms.data

import android.content.Context

class UserPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("rankin_realms", Context.MODE_PRIVATE)

    fun isFavorite(id: String): Boolean = prefs.getBoolean("fav_$id", false)
    fun setFavorite(id: String, favorite: Boolean) = prefs.edit().putBoolean("fav_$id", favorite).apply()

    fun colorMode(): String = prefs.getString("color_mode", "auto") ?: "auto"
    fun setColorMode(value: String) = prefs.edit().putString("color_mode", value).apply()
}
