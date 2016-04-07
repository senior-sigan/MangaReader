package org.seniorsigan.mangareader.data

import android.util.Log
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
                Log.d("MangaSearchRepositoryImpl", "Search: from cache $listFromCache")
                callback(listFromCache)
                network.search(query, { listFromNetwork ->
                    Log.d("MangaSearchRepositoryImpl", "Search: from network $listFromNetwork")
                    callback(listFromNetwork)
                })
            })
        }
    }

    override fun find(url: String, callback: (MangaItem?) -> Unit) {
        async() {
            cache.find(url, { cacheManga ->
                Log.d("MangaSearchRepositoryImpl", "Find: from cache $cacheManga")
                callback(cacheManga)
                network.find(url, { networkManga ->
                    Log.d("MangaSearchRepositoryImpl", "FInd: from network $networkManga")
                    callback(networkManga)
                    cache.update(networkManga)
                })
            })
        }
    }

    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        async() {
            cache.findAll { listFromCache ->
                Log.d("MangaSearchRepositoryImpl", "FindAll: from cache $listFromCache")
                callback(listFromCache)
                network.findAll { listFromNetwork ->
                    Log.d("MangaSearchRepositoryImpl", "FIndAll: from network $listFromNetwork")
                    callback(listFromNetwork)
                    cache.updateAll(listFromNetwork)
                }
            }
        }
    }
}