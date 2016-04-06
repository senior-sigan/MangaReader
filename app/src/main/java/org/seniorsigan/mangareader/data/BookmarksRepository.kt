package org.seniorsigan.mangareader.data

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.models.BookmarkItem
import org.seniorsigan.mangareader.models.MangaItem

class BookmarksRepository(val context: Context) {
    val storage = context.getSharedPreferences("Bookmarks", 0)

    fun save(item: BookmarkItem) {
        with(storage.edit(), {
            putString(item._id, App.toJson(item))
            commit()
        })
    }

    fun remove(item: BookmarkItem) {
        with(storage.edit(), {
            remove(item.manga.url)
            commit()
        })
    }

    fun update(item: BookmarkItem) {
        with(storage.edit(), {
            remove(item.manga.url)
            putString(item._id, App.toJson(item))
            commit()
        })
    }

    fun findAll(): List<BookmarkItem> {
        return storage.all
                .map { it.value as String }
                .map { App.parseJson(it, BookmarkItem::class.java) }
                .filterNotNull()
    }
}