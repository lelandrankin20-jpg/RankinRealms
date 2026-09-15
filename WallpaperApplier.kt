package com.rankinenvironments.realms.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import com.rankinenvironments.realms.model.WallpaperTarget

class WallpaperApplier(context: Context) {
    private val manager = WallpaperManager.getInstance(context)

    fun apply(bitmap: Bitmap, target: WallpaperTarget) {
        val which = when (target) {
            WallpaperTarget.HOME -> WallpaperManager.FLAG_SYSTEM
            WallpaperTarget.LOCK -> WallpaperManager.FLAG_LOCK
            WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        }
        manager.setBitmap(bitmap, null, false, which)
    }
}
