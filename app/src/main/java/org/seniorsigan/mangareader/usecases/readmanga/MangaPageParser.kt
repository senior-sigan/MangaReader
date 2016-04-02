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

class MangaPageParser {
    fun parse(url: String, callback: (MangaItem?) -> Unit) {
        val req = Request.Builder().url(url).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't finish request to $url: $e")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                callback(extract(html))
            }
        })
    }

    private fun extract(html: String?): MangaItem? {
        if (html == null) return null
        val doc = Jsoup.parse(html)
        val description = doc.select("#mangaBox > div[itemscope] > meta[itemprop=description]").first().attr("content")
        val title = doc.select("#mangaBox > div[itemscope] > meta[itemprop=name]").first().attr("content")
        val url = doc.select("#mangaBox > div[itemscope] > meta[itemprop=url]").first().attr("content")
        val images = doc.select(".picture-fotorama > img[itemprop=image]").map { it.attr("src") }
        return MangaItem(
                title = title,
                url = url,
                coverURL = images.firstOrNull(),
                description = description,
                coverURLS = images)
    }
}