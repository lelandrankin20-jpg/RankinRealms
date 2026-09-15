package com.rankinenvironments.realms.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.rankinenvironments.realms.model.CropState
import com.rankinenvironments.realms.wallpaper.CropMath

@Composable
fun CropCanvas(
    bitmap: Bitmap,
    state: CropState,
    onStateChange: (CropState) -> Unit,
    modifier: Modifier = Modifier
) {
    val image = bitmap.asImageBitmap()
    Canvas(
        modifier = modifier.pointerInput(bitmap, state) {
            detectTransformGestures { _, pan, gestureZoom, _ ->
                val newZoom = (state.zoom * gestureZoom).coerceIn(1f, 8f)
                val currentRect = CropMath.visibleRect(
                    bitmap.width,
                    bitmap.height,
                    size.width.coerceAtLeast(1),
                    size.height.coerceAtLeast(1),
                    state
                )
                val nx = state.centerX - (pan.x / size.width.coerceAtLeast(1)) * (currentRect.width().toFloat() / bitmap.width)
                val ny = state.centerY - (pan.y / size.height.coerceAtLeast(1)) * (currentRect.height().toFloat() / bitmap.height)
                onStateChange(CropState(nx.coerceIn(0f, 1f), ny.coerceIn(0f, 1f), newZoom))
            }
        }
    ) {
        val rect = CropMath.visibleRect(
            bitmap.width,
            bitmap.height,
            size.width.toInt().coerceAtLeast(1),
            size.height.toInt().coerceAtLeast(1),
            state
        )
        drawImage(
            image = image,
            srcOffset = IntOffset(rect.left, rect.top),
            srcSize = IntSize(rect.width(), rect.height()),
            dstOffset = IntOffset.Zero,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
            filterQuality = FilterQuality.High
        )
    }
}
