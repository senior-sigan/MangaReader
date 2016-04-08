package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem

class ChaptersCacheRepository(
        private val context: Context
): ChaptersRepository {
    val storage = context.getSharedPreferences("ChaptersCache", 0)

    override fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        val data = storage.getString(manga.url, null)
        val chapters = App.parseJson(data, Chapters::class.java)
        val list = if (chapters != null) {
            chapters.list
        } else {
            emptyList()
        }

        callback(list)
    }

    fun updateAll(manga: MangaItem, chapters: List<ChapterItem>) {
        if (chapters.isEmpty()) return
        with(storage.edit(), {
            putString(manga.url, App.toJson(Chapters(list = chapters)))
            commit()
        })
    }

    private data class Chapters(
            val list: List<ChapterItem>
    )
}