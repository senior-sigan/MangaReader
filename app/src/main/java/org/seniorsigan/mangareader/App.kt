package org.seniorsigan.mangareader

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.jetbrains.anko.alarmManager
import org.seniorsigan.mangareader.data.*
import org.seniorsigan.mangareader.data.cache.BookmarksRepository
import org.seniorsigan.mangareader.data.cache.ChaptersCacheRepository
import org.seniorsigan.mangareader.data.cache.MangaCacheRepository
import org.seniorsigan.mangareader.data.cache.PagesCacheRepository
import org.seniorsigan.mangareader.data.network.ChaptersNetworkRepository
import org.seniorsigan.mangareader.data.network.MangaNetworkRepository
import org.seniorsigan.mangareader.data.network.PagesNetworkRepository
import org.seniorsigan.mangareader.receivers.UpdatesReceiver
import org.seniorsigan.mangareader.usecases.BookmarksManager
import org.seniorsigan.mangareader.usecases.DigestGenerator
import org.seniorsigan.mangareader.usecases.TransportWithCache
import org.seniorsigan.mangareader.usecases.readmanga.MintmangaUrls
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaUrls
import org.seniorsigan.mangareader.usecases.readmanga.SelfmangaUrls
import java.util.*

const val TAG = "MangaReader"
const val INTENT_MANGA = "INTENT_MANGA"

class App: Application() {
    companion object {
        val client = OkHttpClient()

        val mangaSearchController = MangaSearchController()
        lateinit var mangaSourceRepository: MangaSourceRepository

        lateinit var chaptersRepository: ChaptersRepositoryImpl
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
                SelfmangaUrls.name,
                MangaSearchRepositoryImpl(
                        MangaCacheRepository("SelfmangaCache", applicationContext),
                        MangaNetworkRepository(
                                SelfmangaUrls,
                                applicationContext,
                                readmangaConverter)))

        mangaSourceRepository = MangaSourceRepository(applicationContext, mangaSearchController)
        mangaSourceRepository.setDefault(ReadmangaUrls.name)
        setupCheckUpdatesAlarm()
    }

    fun setupCheckUpdatesAlarm() {
        val intent = Intent(UpdatesReceiver.CHECK_UPDATES)
        val alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_DAY,
                alarmIntent)
        Log.d(TAG, "Setup CheckUpdates alarm clock")
    }
}