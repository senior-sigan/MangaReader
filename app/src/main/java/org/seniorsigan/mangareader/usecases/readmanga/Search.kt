package org.seniorsigan.mangareader.usecases.readmanga

import android.util.Log
import okhttp3.*
import org.jsoup.Jsoup
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.data.network.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.usecases.Search
import java.io.IOException

class QuerySearch(val baseURL: String, val converter: ReadmangaMangaApiConverter) {
    val searchURL = "$baseURL/search"

    fun search(query: String, callback: (List<MangaItem>) -> Unit) {
        val body = FormBody.Builder().add("q", query).build()
        val req = Request.Builder().url(searchURL).post(body).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                callback(converter.parse(html, baseURL))
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't load manga by $query from $searchURL: $e")
            }
        })
    }
}

abstract class PopularSearch(val converter: ReadmangaMangaApiConverter): Search {
    abstract val baseURL: String
    val searchURL = "$baseURL/list?sortType=rate"

    override fun search(callback: (List<MangaItem>) -> Unit) {
        val req = Request.Builder().url(searchURL).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                callback(converter.parse(html, baseURL))
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't load popular manga from $searchURL: $e")
            }
        })
    }
}

class ReadmangaSearch(converter: ReadmangaMangaApiConverter) : PopularSearch(converter) {
    companion object {
        val name = "readmanga"
    }

    override val baseURL: String
        get() = "http://readmanga.me"
}

class MintmangaSearch(converter: ReadmangaMangaApiConverter) : PopularSearch(converter) {
    companion object {
        val name = "mintmanga"
    }

    override val baseURL: String
        get() = "http://mintmanga.com/"
}

class SelfmangaSearch(converter: ReadmangaMangaApiConverter) : PopularSearch(converter) {
    companion object {
        val name = "selfmanga"
    }

    override val baseURL: String
        get() = "http://selfmanga.ru/"
}