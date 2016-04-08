package org.seniorsigan.mangareader

import android.app.Application
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.data.*
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.data.cache.ChaptersCacheRepository
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.cache.PagesCacheRepository
import org.seniorsigan.mangareader.data.network.ChaptersNetworkRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.data.network.PagesNetworkRepository
import org.seniorsigan.mangareader.usecases.BookmarksManager
import org.seniorsigan.mangareader.usecases.DigestGenerator
import org.seniorsigan.mangareader.usecases.TransportWithCache
import org.seniorsigan.mangareader.usecases.readmanga.MintmangaUrls
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaUrls
import org.seniorsigan.mangareader.usecases.readmanga.SelfmangaUrls

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"
const val INTENT_MANGA = "INTENT_MANGA"

class App: Application() {
    companion object {
        val client = OkHttpClient()

        val mangaSearchController = MangaSearchController()

        lateinit var chaptersRepository: ChaptersRepository
        lateinit var pagesRepository: PagesRepository
        val digest = DigestGenerator()
        private val gsonBuilder = GsonBuilder()
        private val gson = gsonBuilder.create()
        lateinit var transport: TransportWithCache
        lateinit var bookmarkManager: BookmarksManager
        private val readmangaConverter = ReadmangaMangaApiConverter()

        fun toJson(data: Any?): String {
            return gson.toJson(data)
        }

        fun <T> parseJson(data: String?, clazz: Class<T>): T? {
            try {
                return gson.fromJson(data, clazz)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return null
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        transport = TransportWithCache(applicationContext)
        val bookmarksRepository = BookmarksRepository(applicationContext)
        chaptersRepository = ChaptersRepositoryImpl(ChaptersCacheRepository(applicationContext), ChaptersNetworkRepository(readmangaConverter))
        pagesRepository = PagesRepositoryImpl(PagesCacheRepository(applicationContext), PagesNetworkRepository(readmangaConverter))
        bookmarkManager = BookmarksManager(chaptersRepository, bookmarksRepository)

        mangaSearchController.register(
                ReadmangaUrls.name,
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("ReadmangaCache", applicationContext),
                        MangaNetworkRepository(
                                ReadmangaUrls,
                                applicationContext,
                                readmangaConverter)))

        mangaSearchController.register(
                MintmangaUrls.name,
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("MintmangaCache", applicationContext),
                        MangaNetworkRepository(
                                MintmangaUrls,
                                applicationContext,
                                readmangaConverter)))

        mangaSearchController.register(
                MintmangaUrls.name,
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("SelfmangaCache", applicationContext),
                        MangaNetworkRepository(
                                SelfmangaUrls,
                                applicationContext,
                                readmangaConverter)))
    }
}