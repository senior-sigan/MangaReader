package org.seniorsigan.mangareader.data

import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaSearchRepositoryImpl(
        val cache: MangaCacheRepository,
        val network: MangaNetworkRepository
): MangaSearchRepository {
    override fun findAll(): List<MangaItem> {
        //TODO: call cache then network
        return network.findAll()
    }
}