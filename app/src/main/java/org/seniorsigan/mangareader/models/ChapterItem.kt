package org.seniorsigan.mangareader.models

data class ChapterItem(
        override val _id: Int,
        val url: String,
        val title: String,
        val pages: List<String> = emptyList()
): BaseItem