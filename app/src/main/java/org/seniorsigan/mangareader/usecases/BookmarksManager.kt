package org.seniorsigan.mangareader.usecases

import android.util.Log
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.models.BookmarkItem
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.data.network.ChaptersNetworkRepository

class BookmarksManager(
        private val chaptersRepository: ChaptersRepository,
        private val bookmarksRepository: BookmarksRepository
) {
    fun save(manga: MangaItem) {
        val bookmark = BookmarkItem(manga)
        bookmarksRepository.save(bookmark)
        loadChapters(bookmark)
    }

    fun loadChapters(bookmark: BookmarkItem) {
        chaptersRepository.findAll(bookmark.manga, { chapters ->
            val newBookmark = BookmarkItem(bookmark.manga, chapters)
            bookmarksRepository.update(newBookmark)
            val newChapters = chapters - bookmark.chapters
            Log.d("BookmarksManager", "New chapters $newChapters")
        })
    }

    fun search(callback: (List<MangaItem>) -> Unit) {
        return bookmarksRepository.findAll { bookmarks ->
            callback(bookmarks.map { it.manga })
        }
    }
}