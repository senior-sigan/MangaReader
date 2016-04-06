package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaCacheRepository(
        val cacheName: String,
        val context: Context
): MangaSearchRepository {
    val storage = context.getSharedPreferences(cacheName, 0)

    override fun findAll(): List<MangaItem> {
        return storage.all
                .map { it.value as String }
                .map { App.parseJson(it, MangaItem::class.java) }
                .filterNotNull()
    }

    fun updateAll(mangaList: List<MangaItem>) {
        if (mangaList.isEmpty()) return
        with(storage.edit(), {
            clear()
            mangaList.forEach { manga ->
                putString(manga.url, App.toJson(manga))
            }
            commit()
        })
    }
}