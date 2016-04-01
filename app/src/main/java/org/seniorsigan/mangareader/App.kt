package org.seniorsigan.mangareader

import android.app.Application
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.usecases.readmanga.*

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"

class App: Application() {
    companion object {
        val client = OkHttpClient()
        val popularSearch = with(SearchController(), {
            register(ReadmangaSearch.name, ReadmangaSearch())
            register(MintmangaSearch.name, MintmangaSearch())
            register(SelfmangaSearch.name, SelfmangaSearch())
            this
        })
        val chaptersRepository = ChaptersRepository()
    }
}