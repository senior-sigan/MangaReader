package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.MangaSearchRepository
import org.seniorsigan.mangareader.data.Response
import org.seniorsigan.mangareader.models.MangaItem

class MangaCacheRepository(
        private val cacheName: String,
        private val context: Context
): MangaSearchRepository {
    private val storage = context.getSharedPreferences(cacheName, 0)
    private val detailsStorage = context.getSharedPreferences("$cacheName-details", 0)

    override fun findAll(callback: (Response<List<MangaItem>>) -> Unit) {
        val res: Response<List<MangaItem>> = try {
            val list = storage.all
                    .map { it.value as String }
                    .map { App.parseJson(it, MangaItem::class.java) }
                    .filterNotNull()
                    .sortedBy { it._id }
            Response(data = list, pending = false)
        } catch (e: Exception) {
            Response(data = emptyList(), pending = false, error = "${e.message}", exception = e)
        }

        callback(res)
    }

    override fun find(url: String, callback: (Response<MangaItem?>) -> Unit) {
        val res: Response<MangaItem?> = try {
            val details = detailsStorage.getString(url, null)
            val basic = storage.getString(url, null)
            val manga = App.parseJson(details ?: basic, MangaItem::class.java)
            Response(data = manga, pending = false)
        } catch (e: Exception) {
            Response(data = null, pending = false, error = "${e.message}", exception = e)
        }

        callback(res)
    }

    override fun search(query: String, callback: (Response<List<MangaItem>>) -> Unit) {
        val res: Response<List<MangaItem>> = try {
            val list = storage.all
                    .map { it.value as String }
                    .map { App.parseJson(it, MangaItem::class.java) }
                    .filterNotNull()
                    .filter { it.title.toLowerCase() == query.toLowerCase() }
                    .sortedBy { it._id }
            Response(data = list, pending = false)
        } catch(e: Exception) {
            Response(data = emptyList(), pending = false, error = "${e.message}", exception = e)
        }

        callback(res)
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