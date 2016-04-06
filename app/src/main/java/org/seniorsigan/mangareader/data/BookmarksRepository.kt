package org.seniorsigan.mangareader.data

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.models.MangaItem

class BookmarksRepository(val context: Context) {
    val storage = context.getSharedPreferences("Bookmarks", 0)

    fun save(item: MangaItem) {
        val editor = storage.edit()
        editor.putString(item.url, App.toJson(item))
        editor.commit()
    }

    fun remove(item: MangaItem) {
        val editor = storage.edit()
        editor.remove(item.url)
        editor.commit()
    }

    fun findAll(): List<MangaItem> {
        return storage.all
                .map { it.value as String }
                .map { App.parseJson(it, MangaItem::class.java) }
                .filterNotNull()
    }
}