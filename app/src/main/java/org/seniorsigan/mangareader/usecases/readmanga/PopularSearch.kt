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

class PopularSearch {
    val baseURL = "http://readmanga.me"
    val searchURL = "$baseURL/list?sortType=rate"

    fun search(callback: (List<MangaItem>) -> Unit) {
        val req = Request.Builder().url(searchURL).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                val doc = Jsoup.parse(html)
                val elements = doc.select(".tiles .tile")
                callback(elements.mapIndexed { i, el ->
                    val img = el.select(".img img").first().attr("src")
                    val title = el.select(".desc h3 a").first().text()
                    val url = baseURL + el.select(".desc h3 a").first().attr("href")
                    MangaItem(_id = i, coverURL = img, title = title, url = url)
                })
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't load popular manga from $searchURL: $e")
            }

        })
    }
}