package com.rankinenvironments.realms.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import java.io.File
import kotlin.math.max

object ImageLoader {
    fun decodeSampled(file: File, maxDimension: Int = 2200): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        var sample = 1
        while (max(bounds.outWidth / sample, bounds.outHeight / sample) > maxDimension * 2) sample *= 2
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
            if (android.os.Build.VERSION.SDK_INT >= 26 && bounds.outColorSpace?.isWideGamut == true) {
                inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.DISPLAY_P3)
            }
        }
        return BitmapFactory.decodeFile(file.absolutePath, opts)
            ?: error("Unable to decode ${file.name}")
    }
}
