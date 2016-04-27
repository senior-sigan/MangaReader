package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.models.MangaItem

interface MangaSearchRepository {
    fun findAll(callback: (Response<List<MangaItem>>) -> Unit)
    fun find(url: String, callback: (Response<MangaItem?>) -> Unit)
    fun search(query: String, callback: (Response<List<MangaItem>>) -> Unit)
}