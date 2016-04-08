package org.seniorsigan.mangareader.data.network

import android.net.Uri
import okhttp3.Request
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.PagesRepository
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter

class PagesNetworkRepository(
        val converter: ReadmangaMangaApiConverter
): PagesRepository {
    override fun findAll(url: String, callback: (List<String>) -> Unit) {
        val uri = Uri.parse(url).buildUpon().appendQueryParameter("mature", "1")
        val req = Request.Builder().url(uri.toString()).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body()?.string()
            converter.parseChapter(html)
        } else {
            emptyList()
        }

        callback(list)
    }
}