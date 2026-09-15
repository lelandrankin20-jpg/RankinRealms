package com.rankinenvironments.realms.wallpaper

import android.graphics.Rect
import com.rankinenvironments.realms.model.CropState
import kotlin.math.roundToInt

object CropMath {
    fun visibleRect(
        sourceWidth: Int,
        sourceHeight: Int,
        targetWidth: Int,
        targetHeight: Int,
        state: CropState
    ): Rect {
        require(sourceWidth > 0 && sourceHeight > 0 && targetWidth > 0 && targetHeight > 0)
        val srcAspect = sourceWidth.toFloat() / sourceHeight
        val dstAspect = targetWidth.toFloat() / targetHeight
        val zoom = state.zoom.coerceIn(1f, 8f)

        var visibleW: Float
        var visibleH: Float
        if (srcAspect > dstAspect) {
            visibleH = sourceHeight.toFloat() / zoom
            visibleW = visibleH * dstAspect
        } else {
            visibleW = sourceWidth.toFloat() / zoom
            visibleH = visibleW / dstAspect
        }

        visibleW = visibleW.coerceAtMost(sourceWidth.toFloat())
        visibleH = visibleH.coerceAtMost(sourceHeight.toFloat())

        val halfW = visibleW / 2f
        val halfH = visibleH / 2f
        val centerX = (state.centerX * sourceWidth).coerceIn(halfW, sourceWidth - halfW)
        val centerY = (state.centerY * sourceHeight).coerceIn(halfH, sourceHeight - halfH)

        return Rect(
            (centerX - halfW).roundToInt().coerceAtLeast(0),
            (centerY - halfH).roundToInt().coerceAtLeast(0),
            (centerX + halfW).roundToInt().coerceAtMost(sourceWidth),
            (centerY + halfH).roundToInt().coerceAtMost(sourceHeight)
        )
    }

    fun pan(state: CropState, dxFractionOfVisible: Float, dyFractionOfVisible: Float): CropState =
        state.copy(
            centerX = (state.centerX - dxFractionOfVisible / state.zoom).coerceIn(0f, 1f),
            centerY = (state.centerY - dyFractionOfVisible / state.zoom).coerceIn(0f, 1f)
        )
}
