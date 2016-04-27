package org.seniorsigan.mangareader.data

import org.jetbrains.anko.async
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.models.MangaItem

class MangaSearchRepositoryImpl(
        val cache: MangaCacheRepository,
        val network: MangaNetworkRepository
): MangaSearchRepository {
    override fun search(query: String, callback: (Response<List<MangaItem>>) -> Unit) {
        async() {
            cache.search(query, { listFromCache ->
                callback(listFromCache)
                network.search(query, { listFromNetwork ->
                    callback(listFromNetwork)
                })
            })
        }
    }

    override fun find(url: String, callback: (Response<MangaItem?>) -> Unit) {
        async() {
            cache.find(url, { cacheRes ->
                callback(cacheRes)
                network.find(url, { networkRes ->
                    callback(networkRes)
                    cache.update(networkRes.data)
                })
            })
        }
    }

    override fun findAll(callback: (Response<List<MangaItem>>) -> Unit) {
        async() {
            cache.findAll { cacheRes ->
                callback(cacheRes)
                network.findAll { networkRes ->
                    callback(networkRes)
                    cache.updateAll(networkRes.data)
                }
            }
        }
    }
}