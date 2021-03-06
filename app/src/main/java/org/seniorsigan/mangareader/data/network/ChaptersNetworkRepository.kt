package org.seniorsigan.mangareader.data.network

import okhttp3.Request
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter

class ChaptersNetworkRepository(
        val converter: ReadmangaMangaApiConverter
): ChaptersRepository {
    override fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        val req = Request.Builder().url(manga.url).build()
        val res = App.client.newCall(req).execute()!!
        val list = if (res.isSuccessful) {
            val html = res.body()?.string()
            converter.parseChapterList(html, req.url().uri())
        } else {
            emptyList()
        }

        callback(list)
    }
}