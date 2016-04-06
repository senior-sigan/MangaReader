package org.seniorsigan.mangareader.data.network

import org.seniorsigan.mangareader.models.MangaItem

interface MangaAPIConverter {
    fun parse(data: String?, baseURL: String): List<MangaItem>
}