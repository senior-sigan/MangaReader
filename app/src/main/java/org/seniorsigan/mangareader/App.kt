package org.seniorsigan.mangareader

import android.app.Application
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.data.ChaptersRepositoryImpl
import org.seniorsigan.mangareader.data.MangaSearchController
import org.seniorsigan.mangareader.data.MangaSearchRepositoryImpl
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.data.cache.ChaptersCacheRepository
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.data.network.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.BookmarksManager
import org.seniorsigan.mangareader.usecases.DigestGenerator
import org.seniorsigan.mangareader.usecases.TransportWithCache
import org.seniorsigan.mangareader.data.network.ChaptersNetworkRepository
import org.seniorsigan.mangareader.usecases.readmanga.MangaPageParser
import org.seniorsigan.mangareader.usecases.readmanga.QuerySearch

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"
const val INTENT_MANGA = "INTENT_MANGA"

class App: Application() {
    companion object {
        val client = OkHttpClient()

        val mangaSearchController = MangaSearchController()

        lateinit var chaptersRepository: ChaptersRepositoryImpl
        val mangaPageParser = MangaPageParser()
        val digest = DigestGenerator()
        private val gsonBuilder = GsonBuilder()
        private val gson = gsonBuilder.create()
        lateinit var transport: TransportWithCache
        lateinit var bookmarkManager: BookmarksManager
        private val readmangaConverter = ReadmangaMangaApiConverter()
        val querySearch = QuerySearch("http://readmanga.me", readmangaConverter)

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
        chaptersRepository = ChaptersRepositoryImpl(ChaptersCacheRepository(applicationContext), ChaptersNetworkRepository())
        bookmarkManager = BookmarksManager(chaptersRepository, bookmarksRepository)

        mangaSearchController.register(
                "readmanga",
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("ReadmangaCache", applicationContext),
                        MangaNetworkRepository(
                                "http://readmanga.me/list?sortType=rate",
                                applicationContext,
                                readmangaConverter)))

        mangaSearchController.register(
                "mintmanga",
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("MintmangaCache", applicationContext),
                        MangaNetworkRepository(
                                "http://mintmanga.com/list?sortType=rate",
                                applicationContext,
                                readmangaConverter)))

        mangaSearchController.register(
                "selfmanga",
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("SelfmangaCache", applicationContext),
                        MangaNetworkRepository(
                                "http://selfmanga.ru/list?sortType=rate",
                                applicationContext,
                                readmangaConverter)))
    }
}