package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaCacheRepository(
        private val cacheName: String,
        private val context: Context
): MangaSearchRepository {
    private val storage = context.getSharedPreferences(cacheName, 0)

    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        val list = storage.all
                .map { it.value as String }
                .map { App.parseJson(it, MangaItem::class.java) }
                .filterNotNull()
                .sortedBy { it._id }
        callback(list)
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