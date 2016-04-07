package org.seniorsigan.mangareader

import android.app.Application
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.data.MangaSearchController
import org.seniorsigan.mangareader.data.MangaSearchRepositoryImpl
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.data.network.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.*
import org.seniorsigan.mangareader.usecases.readmanga.*

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"
const val INTENT_MANGA = "INTENT_MANGA"

class App: Application() {
    companion object {
        val client = OkHttpClient()
        lateinit var searchController: SearchController

        val mangaSearchController = MangaSearchController()

        val chaptersRepository = ChaptersRepository()
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
        bookmarkManager = BookmarksManager(chaptersRepository, bookmarksRepository)
        searchController = with(SearchController(), {
            register(ReadmangaSearch.name, ReadmangaSearch(readmangaConverter))
            register(MintmangaSearch.name, MintmangaSearch(readmangaConverter))
            register(SelfmangaSearch.name, SelfmangaSearch(readmangaConverter))
            register(BookmarksSearch.name, BookmarksSearch(bookmarkManager))
            this
        })

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