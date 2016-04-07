package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.models.MangaItem

interface MangaSearchRepository {
    fun findAll(callback: (List<MangaItem>) -> Unit)
    fun find(url: String, callback: (MangaItem?) -> Unit)
    fun search(query: String, callback: (List<MangaItem>) -> Unit)
}