package org.seniorsigan.mangareader.data.network

import okhttp3.Request
import org.jsoup.Jsoup
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import java.net.URI

class ChaptersNetworkRepository: ChaptersRepository {
    override fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        val req = Request.Builder().url(manga.url).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body()?.string()
            parse(html, req.url().uri())
        } else {
            emptyList()
        }

        callback(list)
    }

    private fun parse(html: String?, uri: URI): List<ChapterItem> {
        if (html == null) return emptyList()

        val doc = Jsoup.parse(html)
        val elements = doc.select(".table tbody tr td a")
        return elements.mapIndexed { i, el ->
            val title = el.text()
            val url = uri.resolve(el.attr("href"))
            ChapterItem(_id = i, title = title, url = url.toString())
        }
    }
}