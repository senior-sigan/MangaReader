package org.seniorsigan.mangareader.usecases.readmanga

import org.jsoup.Jsoup
import org.seniorsigan.mangareader.models.MangaItem

class ReadmangaMangaApiConverter {
    fun parseList(data: String?, baseURL: String): List<MangaItem> {
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

    fun parse(data: String?): MangaItem? {
        if (data == null) return null
        val doc = Jsoup.parse(data)
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