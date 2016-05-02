package org.seniorsigan.mangareader.data.cache;

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.Response
import org.seniorsigan.mangareader.data.network.FeedRepository
import org.seniorsigan.mangareader.models.FeedItem

class FeedCacheRepository(
        private val context: Context
): FeedRepository {
    override fun markRead(item: FeedItem) {
        save(item.copy(isRead = true))
    }

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
                val json = App.toJson(
                    if (item._id == 0) {
                        item.copy(_id = storage.all.size)
                    } else {
                        item
                    })
                putString(item.chapter.url, json)
                commit()
            })
        })
    }
}
