package com.rankinenvironments.realms.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rankinenvironments.realms.data.CatalogRepository
import com.rankinenvironments.realms.data.DownloadRepository
import com.rankinenvironments.realms.data.UserPrefs
import com.rankinenvironments.realms.model.*
import com.rankinenvironments.realms.util.WideColor
import com.rankinenvironments.realms.wallpaper.WallpaperApplier
import com.rankinenvironments.realms.wallpaper.WallpaperRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

private sealed interface Route {
    data object Gallery : Route
    data class Detail(val artwork: Artwork) : Route
    data class Frame(val artwork: Artwork) : Route
    data object Settings : Route
}

@Composable
fun RankinRealmsApp() {
    RankinRealmsTheme {
        val context = LocalContext.current
        val catalogRepository = remember { CatalogRepository(context) }
        val downloads = remember { DownloadRepository(context) }
        val prefs = remember { UserPrefs(context) }
        var route by remember { mutableStateOf<Route>(Route.Gallery) }
        var catalog by remember { mutableStateOf<List<Artwork>>(emptyList()) }
        var loadError by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            runCatching { catalogRepository.loadCatalog() }
                .onSuccess { catalog = it }
                .onFailure { loadError = it.message }
        }

        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (val current = route) {
                Route.Gallery -> GalleryScreen(
                    catalog = catalog,
                    loadError = loadError,
                    downloads = downloads,
                    onArtwork = { route = Route.Detail(it) },
                    onSettings = { route = Route.Settings }
                )
                is Route.Detail -> DetailScreen(
                    artwork = current.artwork,
                    downloads = downloads,
                    prefs = prefs,
                    onBack = { route = Route.Gallery },
                    onFrame = { route = Route.Frame(current.artwork) }
                )
                is Route.Frame -> FrameScreen(
                    artwork = current.artwork,
                    downloads = downloads,
                    prefs = prefs,
                    onBack = { route = Route.Detail(current.artwork) }
                )
                Route.Settings -> SettingsScreen(
                    prefs = prefs,
                    downloads = downloads,
                    onBack = { route = Route.Gallery }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryScreen(
    catalog: List<Artwork>,
    loadError: String?,
    downloads: DownloadRepository,
    onArtwork: (Artwork) -> Unit,
    onSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("RANKIN REALMS", fontWeight = FontWeight.Black)
                        Text("Cinematic wallpaper masters", style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = { TextButton(onClick = onSettings) { Text("Settings") } }
            )
        }
    ) { padding ->
        when {
            loadError != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Catalog unavailable: $loadError")
            }
            catalog.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(catalog, key = { it.id }) { artwork ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onArtwork(artwork) },
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column {
                            PreviewImage(
                                artwork = artwork,
                                downloads = downloads,
                                modifier = Modifier.fillMaxWidth().aspectRatio(0.78f),
                                contentScale = ContentScale.Crop
                            )
                            Column(Modifier.padding(12.dp)) {
                                if (artwork.featured) Text("FEATURED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(artwork.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                                Text(artwork.collection, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    artwork: Artwork,
    downloads: DownloadRepository,
    prefs: UserPrefs,
    onBack: () -> Unit,
    onFrame: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var favorite by remember { mutableStateOf(prefs.isFavorite(artwork.id)) }
    var downloading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/jpeg")
    ) { uri ->
        if (uri != null) scope.launch {
            downloading = true
            runCatching { downloads.exportMasterTo(artwork, uri) }
                .onSuccess { toast(context, "Full master saved") }
                .onFailure { toast(context, it.message ?: "Save failed") }
            downloading = false
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(artwork.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
        )
    }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(bottom = 32.dp)
        ) {
            PreviewImage(
                artwork,
                downloads,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.15f),
                contentScale = ContentScale.Fit
            )
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(artwork.collection, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                Text(artwork.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                if (artwork.description.isNotBlank()) Text(artwork.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "${formatPixels(artwork.width, artwork.height)} • ${artwork.colorSpace} source",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(onClick = onFrame, modifier = Modifier.fillMaxWidth()) { Text("Set as wallpaper") }
                Text(
                    if (artwork.masterUrl.isBlank()) "Test build uses the bundled preview for wallpaper application. Final release will download the verified full master." else "Full master will be used when you apply the wallpaper.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = {
                        favorite = !favorite
                        prefs.setFavorite(artwork.id, favorite)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (favorite) "Saved to favorites" else "Add to favorites") }
                OutlinedButton(
                    enabled = (artwork.masterUrl.startsWith("https://") || artwork.masterUrl.startsWith("asset://")) && !downloading,
                    onClick = { exportLauncher.launch("${artwork.id}_master.jpg") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Save full master") }
                if (!(artwork.masterUrl.startsWith("https://") || artwork.masterUrl.startsWith("asset://"))) {
                    Text("Production master URL not configured yet.", style = MaterialTheme.typography.labelSmall)
                }
                if (downloading) LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrameScreen(
    artwork: Artwork,
    downloads: DownloadRepository,
    prefs: UserPrefs,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val renderer = remember { WallpaperRenderer(context, prefs) }
    val applier = remember { WallpaperApplier(context) }
    var preview by remember { mutableStateOf<Bitmap?>(null) }
    var crop by remember { mutableStateOf(CropState()) }
    var mode by remember { mutableStateOf(FrameMode.CROP) }
    var target by remember { mutableStateOf(WallpaperTarget.BOTH) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Frame wallpaper") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
        )
    }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.padding(16.dp).widthIn(max = 390.dp).fillMaxWidth().aspectRatio(9f / 19.5f)
                    .clip(RoundedCornerShape(28.dp)).background(Color.Black)
            ) {
                if (mode == FrameMode.CROP && preview != null) {
                    CropCanvas(preview!!, crop, { crop = it }, Modifier.fillMaxSize())
                } else {
                    PreviewImage(
                        artwork,
                        downloads,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = if (mode == FrameMode.FULL_FIT) ContentScale.Fit else ContentScale.Crop,
                        onBitmap = { preview = it }
                    )
                }
                Box(
                    Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
                        .width(120.dp).height(34.dp).clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.28f))
                )
            }
            if (preview == null) {
                PreviewImage(artwork, downloads, Modifier.size(1.dp), onBitmap = { preview = it })
            }
            Text("Pinch to zoom • drag to choose exactly what is visible", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == FrameMode.CROP, onClick = { mode = FrameMode.CROP }, label = { Text("Crop") })
                FilterChip(selected = mode == FrameMode.FULL_FIT, onClick = { mode = FrameMode.FULL_FIT }, label = { Text("Whole master") })
                FilterChip(selected = mode == FrameMode.SCROLL_HOME, onClick = { mode = FrameMode.SCROLL_HOME; target = WallpaperTarget.HOME }, label = { Text("Scroll") })
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = target == WallpaperTarget.HOME, onClick = { target = WallpaperTarget.HOME }, label = { Text("Home") })
                FilterChip(selected = target == WallpaperTarget.LOCK, enabled = mode != FrameMode.SCROLL_HOME, onClick = { target = WallpaperTarget.LOCK }, label = { Text("Lock") })
                FilterChip(selected = target == WallpaperTarget.BOTH, enabled = mode != FrameMode.SCROLL_HOME, onClick = { target = WallpaperTarget.BOTH }, label = { Text("Both") })
            }
            Spacer(Modifier.height(16.dp))
            Button(
                enabled = !busy,
                onClick = {
                    scope.launch {
                        busy = true
                        progress = 0f
                        runCatching {
                            val master = downloads.ensureMaster(artwork) { progress = it }
                            val bitmap = withContext(Dispatchers.Default) { renderer.render(master, crop, mode) }
                            withContext(Dispatchers.IO) { applier.apply(bitmap, target) }
                            bitmap.recycle()
                        }.onSuccess { toast(context, "Wallpaper applied") }
                            .onFailure { toast(context, it.message ?: "Unable to apply wallpaper") }
                        busy = false
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            ) { Text(if (busy) "Preparing full master…" else "Apply wallpaper") }
            if (busy) LinearProgressIndicator(progress = { progress }, modifier = Modifier.padding(20.dp).fillMaxWidth())
            if (artwork.masterUrl.isBlank()) {
                Text(
                    "TEST MODE — wallpaper application works now using the bundled preview. Production will replace this with the verified full-resolution master download.",
                    Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(prefs: UserPrefs, downloads: DownloadRepository, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf(prefs.colorMode()) }
    val wide = WideColor.screenSupportsWideColor(context)
    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") }, navigationIcon = { TextButton(onClick = onBack) { Text("Back") } })
    }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Color output", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(if (wide) "This display reports wide-color-gamut support." else "This display does not report wide-color-gamut support.")
            listOf("auto" to "Auto", "p3" to "Display P3 when supported", "srgb" to "sRGB compatibility").forEach { (value, label) ->
                FilterChip(selected = mode == value, onClick = { mode = value; prefs.setColorMode(value) }, label = { Text(label) })
            }
            HorizontalDivider()
            Text("Downloaded masters are cached privately on-device so framing does not reduce source quality.")
            OutlinedButton(onClick = {
                scope.launch { downloads.clearDownloadedMasters(); toast(context, "Downloaded masters cleared") }
            }) { Text("Clear downloaded masters") }
            Text("No advertising ID, account, photo-library permission, or broad media permission is required by the MVP.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun toast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
private fun formatPixels(w: Int, h: Int): String = if (w > 0 && h > 0) String.format(Locale.US, "%,d × %,d", w, h) else "Full-resolution master"
