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
    private val detailsStorage = context.getSharedPreferences("$cacheName-details", 0)

    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        val list = storage.all
                .map { it.value as String }
                .map { App.parseJson(it, MangaItem::class.java) }
                .filterNotNull()
                .sortedBy { it._id }
        callback(list)
    }

    override fun find(url: String, callback: (MangaItem?) -> Unit) {
        val details = detailsStorage.getString(url, null)
        val basic = storage.getString(url, null)
        val manga = App.parseJson(details ?: basic, MangaItem::class.java)
        callback(manga)
    }

    override fun search(query: String, callback: (List<MangaItem>) -> Unit) {
        val list = storage.all
                .map { it.value as String }
                .map { App.parseJson(it, MangaItem::class.java) }
                .filterNotNull()
                .filter { it.title.toLowerCase() == query.toLowerCase() }
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

    fun update(item: MangaItem?) {
        if (item == null) return

        with(detailsStorage.edit(), {
            putString(item.url, App.toJson(item))
            commit()
        })
    }
}