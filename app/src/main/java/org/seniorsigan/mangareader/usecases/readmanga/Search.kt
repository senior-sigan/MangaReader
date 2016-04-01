package org.seniorsigan.mangareader.usecases.readmanga

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.models.MangaItem
import java.io.IOException

interface Search {
    fun search(callback: (List<MangaItem>) -> Unit)
}

class SearchController {
    private val engines: MutableMap<String, Search> = hashMapOf()

    fun search(engineName: String, callback: (List<MangaItem>) -> Unit) {
        engines[engineName]?.search(callback)
    }

    fun register(name: String, search: Search): SearchController {
        engines.put(name, search)
        return this
    }

    fun unregister(name: String): SearchController {
        engines.remove(name)
        return this
    }

    fun engineNames(): List<String> = engines.keys.toList()
}

abstract class PopularSearch: Search {
    abstract val baseURL: String
    val searchURL = "$baseURL/list?sortType=rate"

    override fun search(callback: (List<MangaItem>) -> Unit) {
        val req = Request.Builder().url(searchURL).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                callback(parse(html))
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't load popular manga from $searchURL: $e")
            }
        })
    }

    private fun parse(html: String?): List<MangaItem> {
        if (html == null) return emptyList()

        val doc = Jsoup.parse(html)
        val elements = doc.select(".tiles .tile")
        return elements.mapIndexed { i, el ->
            val img = el.select(".img img").first().attr("src")
            val title = el.select(".desc h3 a").first().text()
            val url = baseURL + el.select(".desc h3 a").first().attr("href")
            MangaItem(_id = i, coverURL = img, title = title, url = url)
        }
    }
}

class ReadmangaSearch: PopularSearch() {
    companion object {
        val name = "readmanga"
    }

    override val baseURL: String
        get() = "http://readmanga.me"
}

class MintmangaSearch: PopularSearch() {
    companion object {
        val name = "mintmanga"
    }

    override val baseURL: String
        get() = "http://mintmanga.com/"
}

class SelfmangaSearch: PopularSearch() {
    companion object {
        val name = "selfmanga"
    }

    override val baseURL: String
        get() = "http://selfmanga.ru/"
}