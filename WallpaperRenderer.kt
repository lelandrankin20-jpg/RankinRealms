package com.rankinenvironments.realms.wallpaper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.rankinenvironments.realms.data.UserPrefs
import com.rankinenvironments.realms.model.CropState
import com.rankinenvironments.realms.model.FrameMode
import com.rankinenvironments.realms.util.WideColor
import java.io.File
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.roundToInt

class WallpaperRenderer(
    private val context: Context,
    private val prefs: UserPrefs
) {
    data class TargetSize(val width: Int, val height: Int)

    fun screenSize(): TargetSize {
        val dm = context.resources.displayMetrics
        return TargetSize(dm.widthPixels, dm.heightPixels)
    }

    fun render(file: File, state: CropState, mode: FrameMode, target: TargetSize = screenSize()): Bitmap {
        return when (mode) {
            FrameMode.CROP -> renderCrop(file, state, target)
            FrameMode.FULL_FIT -> renderFullFit(file, target)
            FrameMode.SCROLL_HOME -> renderScrollable(file, target.height)
        }
    }

    private fun createOutput(width: Int, height: Int): Bitmap {
        val cs = WideColor.outputColorSpace(context, prefs.colorMode())
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888, false, cs)
    }

    @Suppress("DEPRECATION")
    private fun renderCrop(file: File, state: CropState, target: TargetSize): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        val crop = CropMath.visibleRect(bounds.outWidth, bounds.outHeight, target.width, target.height, state)
        val decoder = BitmapRegionDecoder.newInstance(file.absolutePath, false)
        val sample = sampleSize(crop.width(), crop.height(), target.width, target.height)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                inPreferredColorSpace = WideColor.outputColorSpace(context, prefs.colorMode())
            }
        }
        val region = decoder.decodeRegion(crop, options) ?: error("Unable to decode selected region")
        decoder.recycle()
        return if (region.width == target.width && region.height == target.height) region else {
            Bitmap.createScaledBitmap(region, target.width, target.height, true).also { if (it !== region) region.recycle() }
        }
    }

    private fun renderFullFit(file: File, target: TargetSize): Bitmap {
        val source = decodeNearTarget(file, target.width, target.height)
        val out = createOutput(target.width, target.height)
        val canvas = Canvas(out)
        canvas.drawColor(Color.BLACK)
        val scale = minOf(target.width.toFloat() / source.width, target.height.toFloat() / source.height)
        val w = (source.width * scale).roundToInt()
        val h = (source.height * scale).roundToInt()
        val left = (target.width - w) / 2
        val top = (target.height - h) / 2
        canvas.drawBitmap(source, null, Rect(left, top, left + w, top + h), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        source.recycle()
        return out
    }

    private fun renderScrollable(file: File, targetHeight: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        val width = ceil(bounds.outWidth.toDouble() * targetHeight.toDouble() / bounds.outHeight.toDouble()).toInt()
        val target = TargetSize(max(screenSize().width, width), targetHeight)
        val source = decodeNearTarget(file, target.width, target.height)
        val out = createOutput(target.width, target.height)
        val canvas = Canvas(out)
        canvas.drawColor(Color.BLACK)
        val scale = target.height.toFloat() / source.height
        val drawW = (source.width * scale).roundToInt()
        val left = (target.width - drawW) / 2
        canvas.drawBitmap(source, null, Rect(left, 0, left + drawW, target.height), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        source.recycle()
        return out
    }

    private fun decodeNearTarget(file: File, targetW: Int, targetH: Int): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.absolutePath, bounds)
        val sample = sampleSize(bounds.outWidth, bounds.outHeight, targetW, targetH)
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                inPreferredColorSpace = WideColor.outputColorSpace(context, prefs.colorMode())
            }
        }
        return BitmapFactory.decodeFile(file.absolutePath, opts) ?: error("Unable to decode master")
    }

    private fun sampleSize(srcW: Int, srcH: Int, dstW: Int, dstH: Int): Int {
        var sample = 1
        while (srcW / (sample * 2) >= dstW && srcH / (sample * 2) >= dstH) sample *= 2
        return sample
    }
}
