package org.seniorsigan.mangareader.usecases

import org.seniorsigan.mangareader.models.MangaItem

interface Search {
    fun search(callback: (List<MangaItem>) -> Unit)
}

class SearchController {
    private val engines: MutableMap<String, Search> = hashMapOf()

    fun search(engineName: String, callback: (List<MangaItem>) -> Unit) {
        engines[engineName]?.search(callback)
    }

    fun register(name: String, search: Search): SearchController {
        engines.put(name, search)
        return this
    }

    fun unregister(name: String): SearchController {
        engines.remove(name)
        return this
    }

    fun engineNames(): List<String> = engines.keys.toList()
}

class BookmarksSearch(private val bookmarksManager: BookmarksManager): Search {
    companion object {
        val name = "bookmarks"
    }

    override fun search(callback: (List<MangaItem>) -> Unit) {
        callback(bookmarksManager.findAllManga())
    }
}