package com.rankinenvironments.realms.util

import android.content.Context
import android.graphics.ColorSpace
import android.os.Build

object WideColor {
    fun screenSupportsWideColor(context: Context): Boolean =
        Build.VERSION.SDK_INT >= 26 && context.resources.configuration.isScreenWideColorGamut

    fun outputColorSpace(context: Context, preference: String): ColorSpace {
        val wide = screenSupportsWideColor(context)
        return when (preference) {
            "p3" -> if (wide) ColorSpace.get(ColorSpace.Named.DISPLAY_P3) else ColorSpace.get(ColorSpace.Named.SRGB)
            "srgb" -> ColorSpace.get(ColorSpace.Named.SRGB)
            else -> if (wide) ColorSpace.get(ColorSpace.Named.DISPLAY_P3) else ColorSpace.get(ColorSpace.Named.SRGB)
        }
    }
}
