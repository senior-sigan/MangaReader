package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem

class ChaptersCacheRepository(
        private val context: Context
): ChaptersRepository {
    override fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        callback(emptyList())
    }

    fun updateAll(manga: MangaItem, chapters: List<ChapterItem>) {

    }
}