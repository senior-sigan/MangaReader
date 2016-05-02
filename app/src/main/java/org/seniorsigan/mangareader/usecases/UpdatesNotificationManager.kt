package org.seniorsigan.mangareader.usecases

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import org.jetbrains.anko.notificationManager
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.ui.MainActivity

class UpdatesNotificationManager(private val context: Context) {
    private val newChapters = mutableListOf<ChapterItem>()
    private var builder: NotificationCompat.Builder? = null
    private val notificationID = 1
    val pending = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT)


    fun notify(chapter: ChapterItem) {
        synchronized(this, {
            newChapters.add(chapter)
            builder().setContentText(chapter.title)
                    .setNumber(newChapters.size)
            context.notificationManager.notify(notificationID, builder().build())
        })
    }

    fun reset() {
        synchronized(this, {
            newChapters.clear()
            builder = null
        })
    }

    fun markRead(chapter: ChapterItem) {
        synchronized(this, {
            newChapters.remove(chapter)
            if (newChapters.isEmpty()) reset()
        })
    }

    private fun builder(): NotificationCompat.Builder {
        return synchronized(this, {
            if (builder == null) {
                builder = NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_book_white_24dp)
                        .setContentIntent(pending)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.new_chapter_notification))
            }
            builder!!
        })
    }
}