package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem

interface ChaptersRepository {
    fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit)
}