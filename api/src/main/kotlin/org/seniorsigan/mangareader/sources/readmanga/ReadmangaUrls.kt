package org.seniorsigan.mangareader.sources.readmanga

import java.net.URI

interface Urls {
    val name: String
    val base: URI
    fun mangaList(offset: Int = 0): URI
    val search: URI
}

object ReadmangaUrls: Urls {
    override val name = "readmanga"
    override val base = URI("http://readmanga.me")
    override fun mangaList(offset: Int) = base.resolve("/list?sortType=rate&offset=$offset")!!
    override val search = base.resolve("/search")!!
}

object MintmangaUrls: Urls {
    override val name = "mintmanga"
    override val base = URI("http://mintmanga.com")
    override fun mangaList(offset: Int) = base.resolve("/list?sortType=rate&offset=$offset")!!
    override val search = base.resolve("/search")!!
}

object SelfmangaUrls: Urls {
    override val name = "selfmanga"
    override val base = URI("http://selfmanga.ru")
    override fun mangaList(offset: Int) = base.resolve("/list?sortType=rate&offset=$offset")!!
    override val search = base.resolve("/search")!!
}