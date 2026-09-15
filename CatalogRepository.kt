package com.rankinenvironments.realms.data

import android.content.Context
import com.rankinenvironments.realms.BuildConfig
import com.rankinenvironments.realms.model.Artwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class CatalogRepository(private val context: Context) {
    suspend fun loadCatalog(): List<Artwork> = withContext(Dispatchers.IO) {
        val json = if (BuildConfig.CATALOG_URL.isBlank()) {
            context.assets.open("catalog.json").bufferedReader().use { it.readText() }
        } else {
            val connection = URL(BuildConfig.CATALOG_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 10_000
            connection.readTimeout = 15_000
            connection.setRequestProperty("Accept", "application/json")
            connection.inputStream.bufferedReader().use { it.readText() }
        }
        parse(json)
    }

    private fun parse(json: String): List<Artwork> {
        val array = JSONArray(json)
        return buildList {
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                add(
                    Artwork(
                        id = o.getString("id"),
                        title = o.getString("title"),
                        collection = o.optString("collection", "REALMS — VOL. I"),
                        description = o.optString("description", ""),
                        previewUrl = o.optString("previewUrl", ""),
                        masterUrl = o.optString("masterUrl", ""),
                        width = o.optInt("width", 0),
                        height = o.optInt("height", 0),
                        bytes = o.optLong("bytes", 0L),
                        colorSpace = o.optString("colorSpace", "Display P3"),
                        featured = o.optBoolean("featured", false)
                    )
                )
            }
        }
    }
}
