package org.seniorsigan.mangareader.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import org.jetbrains.anko.async
import org.jetbrains.anko.connectivityManager
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.TAG

/**
 * Check whether a new chapter of favorite manga was released.
 */
class UpdatesReceiver: BroadcastReceiver() {
    companion object {
        val CHECK_UPDATES = "org.seniorsigan.mangareader.CHECK_UPDATES"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Handle UpdatesReceiver")
        val networkInfo = context.connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                async() {
                    checkUpdates()
                }
            }
        }
    }

    private fun checkUpdates() {
        Log.d(TAG, "Check updates")

        App.bookmarkManager.findAll { bookmarks ->
            bookmarks.forEach { bookmark ->
                App.chaptersRepository.findNew(bookmark.manga, { chapters ->
                    chapters.forEach { chapter ->
                        App.updatesNotification.notify(chapter)
                    }
                })
            }
        }
    }
}