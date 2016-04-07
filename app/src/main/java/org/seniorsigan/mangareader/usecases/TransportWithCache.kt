package org.seniorsigan.mangareader.usecases

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSource
import okio.Okio
import org.seniorsigan.mangareader.App
import java.io.File

class TransportWithCache(val context: Context, val client: OkHttpClient = OkHttpClient()) {
    private val storage = context.getSharedPreferences("TransportCache", 0)
    private val MANGA_CACHE = "manga-cache"

    /**
     * Loads file from internet or cache and returns path to file
     */
    fun load(url: String): Uri? {
        val key = generateName(url)
        if (storage.contains(key)) {
            Log.d("TransportWithCache", "From cache $url")
            return Uri.parse(storage.getString(key, null))
        }

        Log.d("TransportWithCache", "From internet $url")
        val req = Request.Builder().url(url).build()
        val res = client.newCall(req).execute()
        if (res.isSuccessful) {
            val uri = Uri.fromFile(saveOnDisk(url, res.body().source()))
            with(storage.edit(), {
                putString(key, uri.toString())
                commit()
            })
            return uri
        }
        Log.d("TransportWithCache", "Error loading data from $url: ${res.code()}")
        return null
    }

    private fun saveOnDisk(url: String, source: BufferedSource): File {
        val file = File(createDefaultCacheDir(), generateName(url))
        val sink = Okio.sink(file)
        source.readAll(sink)
        source.close()
        sink.close()
        return file
    }

    private fun createDefaultCacheDir(): File {
        val cache = File(context.applicationContext.cacheDir, MANGA_CACHE)
        if (!cache.exists()) {
            cache.mkdirs()
        }
        return cache
    }

    private fun generateName(url: String): String {
        val subtype = if (url.contains(".")) {
            url.substring(url.lastIndexOf(".") + 1)
        } else {
            ""
        }
        val name = App.digest.digest(url)
        return "$name.$subtype"
    }
}