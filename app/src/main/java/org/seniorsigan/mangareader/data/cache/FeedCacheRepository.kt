package org.seniorsigan.mangareader.data.cache;

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.Response
import org.seniorsigan.mangareader.data.network.FeedRepository
import org.seniorsigan.mangareader.models.FeedItem

class FeedCacheRepository(
        private val context: Context
): FeedRepository {
    val storage = context.getSharedPreferences("ChaptersFeed", 0)

    override fun findAll(callback: (Response<List<FeedItem>>) -> Unit) {
        val res: Response<List<FeedItem>> = try {
            val data = storage.all
                    .map { it.value as String }
                    .map { App.parseJson(it, FeedItem::class.java) }
                    .filterNotNull()
                    .sortedBy { it.date }
            Response(data = data, pending = false)
        } catch (e: Exception) {
            Response(data = emptyList(), pending = false, error = "${e.message}", exception = e)
        }

        callback(res)
    }

    override fun save(item: FeedItem) {
        synchronized(this, { // because of getting storage.all.size
            with(storage.edit(), {
                putString(item.chapter.url, App.toJson(item.copy(_id = storage.all.size)))
                commit()
            })
        })
    }
}
