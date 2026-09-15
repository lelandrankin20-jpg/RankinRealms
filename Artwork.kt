package com.rankinenvironments.realms.model

data class Artwork(
    val id: String,
    val title: String,
    val collection: String,
    val description: String,
    val previewUrl: String,
    val masterUrl: String,
    val width: Int,
    val height: Int,
    val bytes: Long,
    val colorSpace: String = "Display P3",
    val featured: Boolean = false
)

enum class WallpaperTarget { HOME, LOCK, BOTH }

enum class FrameMode { CROP, FULL_FIT, SCROLL_HOME }

data class CropState(
    val centerX: Float = 0.5f,
    val centerY: Float = 0.5f,
    val zoom: Float = 1f
)
