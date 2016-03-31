package org.seniorsigan.mangareader

import android.app.Application
import okhttp3.OkHttpClient
import org.seniorsigan.mangareader.usecases.readmanga.ChaptersRepository
import org.seniorsigan.mangareader.usecases.readmanga.PopularSearch

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"

class App: Application() {
    companion object {
        val client = OkHttpClient()
        val search = PopularSearch()
        val chaptersRepository = ChaptersRepository()
    }
}