package org.seniorsigan.mangareader.models

import java.util.*

data class FeedItem(
        override val _id: Int = 0,
        val manga: MangaItem,
        val chapter: ChapterItem,
        val date: Date = Date()
): BaseItem