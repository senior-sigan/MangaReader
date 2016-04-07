package org.seniorsigan.mangareader.data

import android.util.Log
import org.jetbrains.anko.async
import org.seniorsigan.mangareader.data.cache.ChaptersCacheRepository
import org.seniorsigan.mangareader.data.network.ChaptersNetworkRepository
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem

class ChaptersRepositoryImpl(
        private val cache: ChaptersCacheRepository,
        private val network: ChaptersNetworkRepository
): ChaptersRepository {
    override fun findAll(manga: MangaItem, callback: (List<ChapterItem>) -> Unit) {
        async() {
            cache.findAll(manga, { listFromCache ->
                Log.d("ChaptersRepositoryImpl", "From cache $listFromCache")
                callback(listFromCache)
                network.findAll(manga, { listFromNetwork ->
                    Log.d("ChaptersRepositoryImpl", "From network $listFromNetwork")
                    callback(listFromNetwork)
                    cache.updateAll(manga, listFromNetwork)
                })
            })
        }
    }
}