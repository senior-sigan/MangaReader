package org.seniorsigan.mangareader.sources.readmanga

import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.models.PageItem
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.regex.Pattern

class ReadmangaMangaApiConverter {
    private val log = LoggerFactory.getLogger(ReadmangaMangaApiConverter::class.java)

    /**
     * Parses list of manga items such as search, popular, etc.
     */
    fun parseList(data: String?, baseURL: URI): List<MangaItem> {
        if (data == null) return emptyList()

        val doc = Jsoup.parse(data)
        val elements = doc.select(".tiles .tile")
        return elements.toList().mapIndexed { i, el ->
            val img = el.select(".img img").first().attr("src")
            val title = el.select(".desc h3 a").first().text()
            val url = baseURL.resolve(el.select(".desc h3 a").first().attr("href"))
            MangaItem(coverURL = img, title = title, url = url)
        }
    }

    /**
     * Parses manga page and retrieve detailed info about manga
     */
    fun parseManga(html: String?, uri: URI): MangaItem? {
        if (html == null) return null
        try {
            val doc = Jsoup.parse(html)
            val description = doc.select("#mangaBox > div[itemscope] > meta[itemprop=description]").first().attr("content")
            val title = doc.select("#mangaBox > div[itemscope] > meta[itemprop=name]").first().attr("content")
            val url = doc.select("#mangaBox > div[itemscope] > meta[itemprop=url]").first().attr("content")
            val images = doc.select(".picture-fotorama > img[itemprop=image]").map { it.attr("src") }
            return MangaItem(
                    title = title,
                    url = URI(url),
                    coverURL = images.firstOrNull(),
                    description = description,
                    coverURLS = images,
                    chapters = parseChapterList(doc, uri))
        } catch (e: Exception) {
            log.error("Can't parseManga: ${e.message}", e)
            return null
        }
    }

    /**
     * Parses manga page and retrieve list of available chapters
     */
    fun parseChapterList(html: String?, uri: URI): List<ChapterItem> {
        if (html == null) return emptyList()

        val doc = Jsoup.parse(html)
        return parseChapterList(doc, uri)
    }

    fun parseChapterList(doc: Document, uri: URI): List<ChapterItem> {
        val elements = doc.select(".table tbody tr td a")
        return elements.map { el ->
            val title = el.text()
            val url = uri.resolve(el.attr("href"))
            ChapterItem(title = title, url = url)
        }
    }

    /**
     * Parses chapter page and retrieve list of pages-images.
     */
    fun parseChapter(html: String?): List<PageItem> {
        if (html == null) return emptyList()
        val data = extract(html) ?: return emptyList()
        val doc = Jsoup.parse(html) ?: return emptyList()

        val commentsPerPage = doc.select("div.cm").map { el ->
            val cn = el.classNames().findLast { name ->
                name.startsWith("cm_")
            } ?: return@map null
            val pageID = cn.substring(3).toIntOrNull() ?: return@map null
            Pair(pageID, el)
        }.filterNotNull().sortedBy { it.first }.map {
            it.second.select("div > span")
                    .map(Element::ownText)
                    .filterNotNull()
                    .map(String::trim)
                    .filterNot(String::isBlank)
        }

        val pages = arrayListOf<Page>()
        with(JSONArray(data), {
            (0..length() - 1)
                    .map { getJSONArray(it) }
                    .mapTo(pages) {
                        Page(
                                host = it.getString(1),
                                path = it.getString(0),
                                item = it.getString(2)
                        )
                    }
        })

        return pages.zip(commentsPerPage).map { pair ->
            PageItem(
                    pictureURL = pair.first.uri,
                    comments = pair.second
            )
        }
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
        val uri: URI
            get() = URI("$host$path$item")
    }
}