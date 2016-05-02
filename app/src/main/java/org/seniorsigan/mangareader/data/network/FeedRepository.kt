package org.seniorsigan.mangareader.data.network

import org.seniorsigan.mangareader.data.Response
import org.seniorsigan.mangareader.models.FeedItem

interface FeedRepository {
    fun findAll(callback: (Response<List<FeedItem>>) -> Unit)
    fun save(item: FeedItem)
    fun markRead(item: FeedItem)
}