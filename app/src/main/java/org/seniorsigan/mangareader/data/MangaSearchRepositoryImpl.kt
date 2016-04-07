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
    override fun findAll(callback: (List<MangaItem>) -> Unit) {
        async() {
            cache.findAll { listFromCache ->
                Log.d("MangaSearchRepositoryImpl", "From cache $listFromCache")
                callback(listFromCache)
                network.findAll { listFromNetwork ->
                    Log.d("MangaSearchRepositoryImpl", "From network $listFromNetwork")
                    callback(listFromNetwork)
                    cache.updateAll(listFromNetwork)
                }
            }
        }
    }
}