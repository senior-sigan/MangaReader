package org.seniorsigan.mangareader.data.cache

import android.content.Context
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.data.PagesRepository

class PagesCacheRepository(
        val context: Context
): PagesRepository {
    private val storage = context.getSharedPreferences("PagesCache", 0)

    override fun findAll(url: String, callback: (List<String>) -> Unit) {
        val data = storage.getString(url, null)
        val pages = App.parseJson(data, Pages::class.java)
        val list = if (pages != null) {
            pages.list
        } else {
            emptyList()
        }

        callback(list)
    }

    fun updateAll(url: String, pages: List<String>) {
        if (pages.isEmpty()) return
        with(storage.edit(), {
            putString(url, App.toJson(Pages(list = pages)))
            commit()
        })
    }

    private data class Pages(
            val list: List<String>
    )
}