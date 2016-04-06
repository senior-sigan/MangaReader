package org.seniorsigan.mangareader

import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.data.BookmarksRepository
import org.seniorsigan.mangareader.usecases.readmanga.*

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"
const val INTENT_MANGA_URL = "INTENT_MANGA_URL"

const val RC_SEARCH = 0

class App: Application() {
    companion object {
        val client = OkHttpClient()
        val popularSearch = with(SearchController(), {
            register(ReadmangaSearch.name, ReadmangaSearch())
            register(MintmangaSearch.name, MintmangaSearch())
            register(SelfmangaSearch.name, SelfmangaSearch())
            this
        })

        val querySearch = QuerySearch("http://readmanga.me")
        val chaptersRepository = ChaptersRepository()
        val mangaPageParser = MangaPageParser()
        val digest = DigestGenerator()
        private val gsonBuilder = GsonBuilder()
        private val gson = gsonBuilder.create()
        lateinit var transport: TransportWithCache
        lateinit var bookmarksRepository: BookmarksRepository

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
        bookmarksRepository = BookmarksRepository(applicationContext)
    }
}