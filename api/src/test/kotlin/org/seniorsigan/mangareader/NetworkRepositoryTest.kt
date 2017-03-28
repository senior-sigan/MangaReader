package org.seniorsigan.mangareader

import org.junit.Test
import org.seniorsigan.mangareader.sources.NetworkRepository
import org.seniorsigan.mangareader.sources.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.sources.readmanga.ReadmangaUrls
import java.net.URI

class NetworkRepositoryTest {
//    @Test
    fun Should_LoadPopular() {
        val repo = NetworkRepository(ReadmangaUrls, ReadmangaMangaApiConverter())
        repo.getPopular().blockingSubscribe {
            println(it)
        }
    }

    @Test
    fun Should_LoadPages() {
        val repo = NetworkRepository(ReadmangaUrls, ReadmangaMangaApiConverter())
        repo.getPages(URI("http://readmanga.me/fairytail/vol61/527")).blockingSubscribe {
            println(it)
        }
    }
}