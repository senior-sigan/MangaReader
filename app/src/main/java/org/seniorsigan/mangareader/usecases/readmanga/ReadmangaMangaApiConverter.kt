package org.seniorsigan.mangareader.usecases.readmanga

import org.json.JSONArray
import org.jsoup.Jsoup
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import java.net.URI
import java.util.regex.Pattern

class ReadmangaMangaApiConverter {
    /**
     * Parses list of manga items such as search, popular, etc.
     */
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

    /**
     * Parses manga page and retrieve detailed info about manga
     */
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

    /**
     * Parses manga page and retrieve list of available chapters
     */
    fun parseChapterList(html: String?, uri: URI): List<ChapterItem> {
        if (html == null) return emptyList()

        val doc = Jsoup.parse(html)
        val elements = doc.select(".table tbody tr td a")
        return elements.mapIndexed { i, el ->
            val title = el.text()
            val url = uri.resolve(el.attr("href"))
            ChapterItem(_id = i, title = title, url = url.toString())
        }
    }

    /**
     * Parses chapter page and retrieve list of pages-images.
     */
    fun parseChapter(html: String?): List<String> {
        if (html == null) return emptyList()
        val data = extract(html) ?: return emptyList()

        val pages = arrayListOf<Page>()
        with(JSONArray(data), {
            for (i in 0..length()-1) {
                val page = getJSONArray(i)
                pages.add(Page(
                        host = page.getString(1),
                        path = page.getString(0),
                        item = page.getString(2)
                ))
            }
        })

        return pages.map { it.uri }
    }

    private fun extract(html: String): String? {
        val regexp = "\\[\\[(.*?)\\]\\]"

        val pattern = Pattern.compile(regexp)
        val matcher = pattern.matcher(html)
        if (matcher.find()) {
            return matcher.group(0)
        } else {
            return null
        }
    }

    private data class Page(
            val host: String,
            val path: String,
            val item: String
    ) {
        val uri: String
            get() = "$host$path$item"
    }
}