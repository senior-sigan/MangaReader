package org.seniorsigan.mangareader.models

data class BookmarkItem(
        val manga: MangaItem,
        val chapters: List<ChapterItem> = emptyList()
) {
    val _id: String
        get() = manga.url
}