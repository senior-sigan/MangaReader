package org.seniorsigan.mangareader.data.network

import android.content.Context
import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.readmanga.Urls

class MangaNetworkRepository(
        val urls: Urls,
        val context: Context,
        val converter: ReadmangaMangaApiConverter
): MangaSearchRepository {
    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        val req = Request.Builder().url(urls.mangaList).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body().string()
            converter.parseList(html, urls.base)
        } else {
            Log.e("MangaNetworkRepository", "Can't load ${urls.mangaList}: ${res.code()}")
            emptyList()
        }

        callback(list)
    }

    override fun find(url: String, callback: (MangaItem?) -> Unit) {
        val req = Request.Builder().url(url).build()
        val res = App.client.newCall(req).execute()!!
        val manga = if (res.isSuccessful) {
            val html = res.body().string()
            converter.parse(html)
        } else {
            Log.e("MangaNetworkRepository", "Can't load $url: ${res.code()}")
            null
        }

        callback(manga)
    }

    override fun search(query: String, callback: (List<MangaItem>) -> Unit) {
        val body = FormBody.Builder().add("q", query).build()
        val req = Request.Builder().url(urls.search).post(body).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body().string()
            converter.parseList(html, urls.base)
        } else {
            Log.e("MangaNetworkRepository", "Can't load ${urls.search}: ${res.code()}")
            emptyList()
        }

        callback(list)
    }
}