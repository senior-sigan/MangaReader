package org.seniorsigan.mangareader.data

import org.jetbrains.anko.async
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaSearchRepositoryImpl(
        val cache: MangaCacheRepository,
        val network: MangaNetworkRepository
): MangaSearchRepository {
    override fun search(query: String, callback: (List<MangaItem>) -> Unit) {
        async() {
            cache.search(query, { listFromCache ->
                callback(listFromCache)
                network.search(query, { listFromNetwork ->
                    callback(listFromNetwork)
                })
            })
        }
    }

    override fun find(url: String, callback: (MangaItem?) -> Unit) {
        async() {
            cache.find(url, { cacheManga ->
                callback(cacheManga)
                network.find(url, { networkManga ->
                    callback(networkManga)
                    cache.update(networkManga)
                })
            })
        }
    }

    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        async() {
            cache.findAll { listFromCache ->
                callback(listFromCache)
                network.findAll { listFromNetwork ->
                    callback(listFromNetwork)
                    cache.updateAll(listFromNetwork)
                }
            }
        }
    }
}