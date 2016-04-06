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
        val converter: MangaAPIConverter
): MangaSearchRepository {
    val baseURL = Uri.parse(apiURL).host

    override fun findAll(): List<MangaItem> {
        val req = Request.Builder().url(apiURL).build()
        val res = App.client.newCall(req).execute()!!
        if (res.isSuccessful) {
            val html = res.body().string()
            return converter.parse(html, baseURL)
        } else {
            return emptyList()
        }
    }
}