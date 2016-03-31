package org.seniorsigan.mangareader.models

data class MangaItem(
        override val _id: Int,
        val coverURL: String,
        val title: String,
        val url: String
): BaseItem