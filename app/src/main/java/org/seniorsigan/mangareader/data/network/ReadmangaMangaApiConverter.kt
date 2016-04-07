package org.seniorsigan.mangareader.data.network

import org.jsoup.Jsoup
import org.seniorsigan.mangareader.models.MangaItem

class ReadmangaMangaApiConverter {
    fun parse(data: String?, baseURL: String): List<MangaItem> {
        if (data == null) return emptyList()

        val doc = Jsoup.parse(data)
        val elements = doc.select(".tiles .tile")
        return elements.mapIndexed { i, el ->
            val img = el.select(".img img").first().attr("src")
            val title = el.select(".desc h3 a").first().text()
            val url = baseURL + el.select(".desc h3 a").first().attr("href")
            MangaItem(_id = i, coverURL = img, title = title, url = url)
        }
    }
}