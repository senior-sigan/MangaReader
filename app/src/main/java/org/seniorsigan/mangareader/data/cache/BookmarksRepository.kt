package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.models.BookmarkItem

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

    fun findAll(callback: (List<BookmarkItem>) -> Unit) {
        val list = storage.all
                .map { it.value as String }
                .map { App.parseJson(it, BookmarkItem::class.java) }
                .filterNotNull()

        callback(list)
    }
}