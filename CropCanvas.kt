package com.rankinenvironments.realms.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
    val latestState by rememberUpdatedState(state)
    val latestOnStateChange by rememberUpdatedState(onStateChange)

    Canvas(
        modifier = modifier.pointerInput(bitmap) {
            detectTransformGestures { _, pan, gestureZoom, _ ->
                val current = latestState
                val currentRect = CropMath.visibleRect(
                    bitmap.width,
                    bitmap.height,
                    size.width.coerceAtLeast(1),
                    size.height.coerceAtLeast(1),
                    current
                )

                val newZoom = (current.zoom * gestureZoom).coerceIn(1f, 8f)
                val nx = current.centerX -
                    (pan.x / size.width.coerceAtLeast(1)) *
                    (currentRect.width().toFloat() / bitmap.width)
                val ny = current.centerY -
                    (pan.y / size.height.coerceAtLeast(1)) *
                    (currentRect.height().toFloat() / bitmap.height)

                latestOnStateChange(
                    CropState(
                        centerX = nx.coerceIn(0f, 1f),
                        centerY = ny.coerceIn(0f, 1f),
                        zoom = newZoom
                    )
                )
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
