package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.models.MangaItem

interface MangaSearchRepository {
    fun findAll(callback: (List<MangaItem>) -> Unit)
}