package org.seniorsigan.mangareader.usecases

import android.util.Log
import org.seniorsigan.mangareader.data.ChaptersRepository
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.models.BookmarkItem
import org.seniorsigan.mangareader.models.MangaItem

class BookmarksManager(
        private val chaptersRepository: ChaptersRepository,
        private val bookmarksRepository: BookmarksRepository
) {
    fun save(manga: MangaItem) {
        val bookmark = BookmarkItem(manga)
        bookmarksRepository.save(bookmark)
        loadChapters(bookmark)
    }

    fun saveOrRemove(manga: MangaItem) {
        bookmarksRepository.find(manga.url, { bookmark ->
            if (bookmark != null) {
                bookmarksRepository.remove(bookmark)
            } else {
                save(manga)
            }
        })
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

    fun findAll(callback: (List<BookmarkItem>) -> Unit) {
        bookmarksRepository.findAll(callback)
    }

    fun find(manga: MangaItem, callback: (BookmarkItem?) -> Unit) {
        bookmarksRepository.find(manga.url, callback)
    }
}