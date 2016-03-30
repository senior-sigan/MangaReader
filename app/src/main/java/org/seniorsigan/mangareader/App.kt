package org.seniorsigan.mangareader

import android.app.Application
import okhttp3.OkHttpClient

const val TAG = "MangaReader"
const val SHARED_URL = "SHARED_URL_INTENT"

class App: Application() {
    companion object {
        val client = OkHttpClient()
    }
}