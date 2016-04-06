package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.models.MangaItem

interface MangaSearchRepository {
    fun findAll(): List<MangaItem>
}