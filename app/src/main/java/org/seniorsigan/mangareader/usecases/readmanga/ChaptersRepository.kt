package org.seniorsigan.mangareader.usecases.readmanga

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import java.io.IOException

class ChaptersRepository {
    fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        val req = Request.Builder().url(manga.url).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                val doc = Jsoup.parse(html)
                val elements = doc.select(".table tbody tr td a")
                callback(elements.mapIndexed { i, el ->
                    val title = el.text()
                    val url = req.url().uri().resolve(el.attr("href"))
                    ChapterItem(_id = i, title = title, url = url.toString())
                })
            }

            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't load chapters for manga ${manga.url}: $e")
            }
        })
    }
}