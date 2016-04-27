package org.seniorsigan.mangareader.data.network

import android.content.Context
import android.util.Log
import okhttp3.FormBody
import okhttp3.Request
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.data.Response
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.readmanga.Urls

class MangaNetworkRepository(
        val urls: Urls,
        val context: Context,
        val converter: ReadmangaMangaApiConverter
): MangaSearchRepository {
    override fun findAll(callback: (Response<List<MangaItem>>) -> Unit) {
        val response: Response<List<MangaItem>> = try {
            val req = Request.Builder().url(urls.mangaList).build()
            val res = App.client.newCall(req).execute()!!
            if (res.isSuccessful) {
                val html = res.body().string()
                val list = converter.parseList(html, urls.base)
                Response(data = list, pending = false)
            } else {
                Log.e("MangaNetworkRepository", "Can't load ${urls.mangaList}: ${res.code()}")
                Response(data = emptyList(), pending = false, error = "Can't load ${urls.mangaList}: ${res.code()}")
            }
        } catch (e: Exception) {
            Response(data = emptyList(), pending = false, error = "${e.message}", exception = e)
        }

        callback(response)
    }

    override fun find(url: String, callback: (Response<MangaItem?>) -> Unit) {
        val response: Response<MangaItem?> = try {
            val req = Request.Builder().url(url).build()
            val res = App.client.newCall(req).execute()!!
            if (res.isSuccessful) {
                val html = res.body().string()
                val manga = converter.parse(html)
                Response(data = manga, pending = false)
            } else {
                Log.e("MangaNetworkRepository", "Can't load $url: ${res.code()}")
                Response(data = null, pending = false, error = "Can't load $url: ${res.code()}")
            }
        } catch (e: Exception) {
            Response(data = null, pending = false, error = "${e.message}", exception = e)
        }

        callback(response)
    }

    override fun search(query: String, callback: (Response<List<MangaItem>>) -> Unit) {
        val response: Response<List<MangaItem>> = try {
            val body = FormBody.Builder().add("q", query).build()
            val req = Request.Builder().url(urls.search).post(body).build()
            val res = App.client.newCall(req).execute()!!
            if (res.isSuccessful) {
                val html = res.body().string()
                val list = converter.parseList(html, urls.base)
                Response(data = list, pending = false)
            } else {
                Log.e("MangaNetworkRepository", "Can't load ${urls.search}: ${res.code()}")
                Response(data = emptyList(), pending = false, error = "Can't load ${urls.search}: ${res.code()}")
            }
        } catch (e: Exception) {
            Response(data = emptyList(), pending = false, error = "${e.message}", exception = e)
        }

        callback(response)
    }
}