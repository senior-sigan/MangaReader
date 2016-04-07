package org.seniorsigan.mangareader.data.network

import android.content.Context
import android.net.Uri
import okhttp3.Request
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaNetworkRepository(
        val apiURL: String,
        val context: Context,
        val converter: ReadmangaMangaApiConverter
): MangaSearchRepository {
    private val uri = Uri.parse(apiURL)
    private val baseURL = Uri.Builder()
            .scheme(uri.scheme)
            .authority(uri.authority)
            .toString()

    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        val req = Request.Builder().url(apiURL).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body().string()
            converter.parse(html, baseURL)
        } else {
            emptyList()
        }

        callback(list)
    }
}