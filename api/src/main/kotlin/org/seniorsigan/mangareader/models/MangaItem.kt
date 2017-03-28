package org.seniorsigan.mangareader.models

import java.net.URI

data class MangaItem(
        val coverURL: String?,
        val title: String,
        val url: URI,
        val description: String = "",
        val coverURLS: List<String> = emptyList(),
        val chapters: List<ChapterItem> = emptyList()
)

data class ChapterItem(
        val url: URI,
        val title: String,
        val pages: List<PageItem> = emptyList()
)

data class PageItem(
        val pictureURL: URI,
        val comments: List<String>
)