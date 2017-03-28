package org.seniorsigan.mangareader.sources

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert
import org.junit.Test
import org.seniorsigan.mangareader.sources.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.sources.readmanga.ReadmangaUrls

class ReadmangaSourceTest {
    val client = OkHttpClient()

    @Test
    fun Should_RetrievePopularList() {
        val converter = ReadmangaMangaApiConverter()
        val req = Request.Builder().url(ReadmangaUrls.mangaList().toURL()).build()
        val res = client.newCall(req).execute()!!
        val html = res.body()?.string()
        val items = converter
                .parseList(html, ReadmangaUrls.base)
                .filterNotNull()
        println(items)
        Assert.assertFalse(items.isEmpty())
    }

}
