package org.seniorsigan.mangareader.usecases.readmanga

import android.util.Log
import org.seniorsigan.mangareader.data.BookmarksRepository
import org.seniorsigan.mangareader.models.BookmarkItem
import org.seniorsigan.mangareader.models.MangaItem

class BookmarksManager(
        val chaptersRepository: ChaptersRepository,
        val bookmarksRepository: BookmarksRepository
) {
    fun save(manga: MangaItem) {
        val bookmark = BookmarkItem(manga)
        bookmarksRepository.save(bookmark)
        loadChapters(bookmark)
    }

    fun loadChapters(bookmark: BookmarkItem) {
        chaptersRepository.findAll(bookmark.manga.url, { chapters ->
            val newBookmark = BookmarkItem(bookmark.manga, chapters)
            bookmarksRepository.update(newBookmark)
            val newChapters = chapters - bookmark.chapters
            Log.d("BookmarksManager", "New chapters $newChapters")
        })
    }

    fun findAllManga(): List<MangaItem> {
        return bookmarksRepository.findAll().map { it.manga }
    }
}