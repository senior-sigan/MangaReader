package org.seniorsigan.mangareader

import android.app.Application
import android.util.Log
import okhttp3.OkHttpClient
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
    }
}