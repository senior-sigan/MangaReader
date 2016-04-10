package org.seniorsigan.mangareader.data

import org.jetbrains.anko.async
import org.seniorsigan.mangareader.data.cache.PagesCacheRepository
import org.seniorsigan.mangareader.data.network.PagesNetworkRepository

class PagesRepositoryImpl(
        val cache: PagesCacheRepository,
        val network: PagesNetworkRepository
): PagesRepository {
    override fun findAll(url: String, callback: (List<String>) -> Unit) {
        async() {
            cache.findAll(url, { pagesCache ->
                if (pagesCache.isNotEmpty()) callback(pagesCache)
                network.findAll(url, { pagesNetwork ->
                    if (pagesCache.isEmpty()) callback(pagesNetwork)
                    cache.updateAll(url, pagesNetwork)
                })
            })
        }
    }
}