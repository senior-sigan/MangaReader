package org.seniorsigan.mangareader.usecases.readmanga

interface Urls {
    val name: String
    val base: String
    val mangaList: String
    val search: String
}

object ReadmangaUrls: Urls {
    override val name = "readmanga"
    override val base = "http://readmanga.me"
    override val mangaList = "http://readmanga.me/list?sortType=rate"
    override val search = "$base/search"
}

object MintmangaUrls: Urls {
    override val name = "mintmanga"
    override val base = "http://mintmanga.com"
    override val mangaList = "http://mintmanga.com/list?sortType=rate"
    override val search = "$base/search"
}

object SelfmangaUrls: Urls {
    override val name = "selfmanga"
    override val base = "http://selfmanga.ru"
    override val mangaList = "http://selfmanga.ru/list?sortType=rate"
    override val search = "$base/search"
}