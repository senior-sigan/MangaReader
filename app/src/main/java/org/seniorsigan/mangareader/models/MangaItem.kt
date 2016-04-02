package org.seniorsigan.mangareader.models

data class MangaItem(
        override val _id: Int = 0,
        val coverURL: String?,
        val title: String,
        val url: String,
        val description: String = "",
        val coverURLS: List<String> = emptyList()
): BaseItem