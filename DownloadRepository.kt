package com.rankinenvironments.realms.data

import android.content.Context
import android.net.Uri
import com.rankinenvironments.realms.model.Artwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadRepository(private val context: Context) {
    private val masterDir = File(context.filesDir, "masters").apply { mkdirs() }
    private val previewDir = File(context.cacheDir, "previews").apply { mkdirs() }

    fun masterFile(artwork: Artwork): File = File(masterDir, "${artwork.id}.jpg")
    fun previewFile(artwork: Artwork): File = File(previewDir, "${artwork.id}.jpg")

    suspend fun ensureMaster(artwork: Artwork, onProgress: (Float) -> Unit = {}): File =
        withContext(Dispatchers.IO) {
            val out = masterFile(artwork)
            if (out.exists() && out.length() > 0) return@withContext out
            when {
                artwork.masterUrl.startsWith("asset://") -> {
                    val asset = artwork.masterUrl.removePrefix("asset://")
                    context.assets.open(asset).use { input ->
                        out.outputStream().buffered(1024 * 1024).use { output -> input.copyTo(output, 1024 * 1024) }
                    }
                    onProgress(1f)
                }
                artwork.masterUrl.startsWith("https://") -> download(artwork.masterUrl, out, onProgress)
                else -> {
                    // Development fallback: use the bundled preview so the native
                    // WallpaperManager flow can be tested before production CDN
                    // master URLs are configured. Production builds should never
                    // rely on this branch for final-quality delivery.
                    val asset = artwork.previewUrl.removePrefix("asset://")
                    if (!artwork.previewUrl.startsWith("asset://")) {
                        error("Master URL is not configured for ${artwork.title}.")
                    }
                    context.assets.open(asset).use { input ->
                        out.outputStream().buffered(1024 * 1024).use { output ->
                            input.copyTo(output, 1024 * 1024)
                        }
                    }
                    onProgress(1f)
                }
            }
            out
        }

    suspend fun ensurePreview(artwork: Artwork): File = withContext(Dispatchers.IO) {
        val out = previewFile(artwork)
        if (out.exists() && out.length() > 0) return@withContext out
        if (artwork.previewUrl.startsWith("asset://")) {
            val asset = artwork.previewUrl.removePrefix("asset://")
            context.assets.open(asset).use { input -> out.outputStream().use(input::copyTo) }
        } else if (artwork.previewUrl.startsWith("https://")) {
            download(artwork.previewUrl, out) {}
        } else {
            throw IllegalStateException("Preview URL is not configured for ${artwork.title}.")
        }
        out
    }

    suspend fun exportMasterTo(artwork: Artwork, uri: Uri) = withContext(Dispatchers.IO) {
        val file = ensureMaster(artwork)
        context.contentResolver.openOutputStream(uri, "w")!!.use { output ->
            file.inputStream().use { it.copyTo(output, 1024 * 1024) }
        }
    }

    suspend fun clearDownloadedMasters() = withContext(Dispatchers.IO) {
        masterDir.listFiles()?.forEach { it.delete() }
    }

    fun downloadedBytes(): Long = masterDir.listFiles()?.sumOf { it.length() } ?: 0L

    private fun download(url: String, out: File, onProgress: (Float) -> Unit) {
        val temp = File(out.parentFile, out.name + ".part")
        if (temp.exists()) temp.delete()
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 15_000
        c.readTimeout = 60_000
        c.instanceFollowRedirects = true
        c.setRequestProperty("User-Agent", "RankinRealms/1.0")
        c.connect()
        if (c.responseCode !in 200..299) error("HTTP ${c.responseCode}")
        val total = c.contentLengthLong.coerceAtLeast(1L)
        var done = 0L
        c.inputStream.buffered(1024 * 1024).use { input ->
            temp.outputStream().buffered(1024 * 1024).use { output ->
                val buffer = ByteArray(1024 * 1024)
                while (true) {
                    val n = input.read(buffer)
                    if (n <= 0) break
                    output.write(buffer, 0, n)
                    done += n
                    onProgress((done.toDouble() / total.toDouble()).toFloat().coerceIn(0f, 1f))
                }
            }
        }
        if (!temp.renameTo(out)) {
            temp.copyTo(out, overwrite = true)
            temp.delete()
        }
        c.disconnect()
    }
}
