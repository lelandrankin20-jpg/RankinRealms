package com.rankinenvironments.realms.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.rankinenvironments.realms.data.DownloadRepository
import com.rankinenvironments.realms.model.Artwork
import com.rankinenvironments.realms.util.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PreviewImage(
    artwork: Artwork,
    downloads: DownloadRepository,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onBitmap: (Bitmap?) -> Unit = {}
) {
    var bitmap by remember(artwork.id) { mutableStateOf<Bitmap?>(null) }
    var failed by remember(artwork.id) { mutableStateOf(false) }

    LaunchedEffect(artwork.id) {
        runCatching {
            val file = downloads.ensurePreview(artwork)
            withContext(Dispatchers.IO) { ImageLoader.decodeSampled(file) }
        }.onSuccess {
            bitmap = it
            onBitmap(it)
        }.onFailure { failed = true }
    }

    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = artwork.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
            )
            failed -> Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
            else -> CircularProgressIndicator()
        }
    }
}
