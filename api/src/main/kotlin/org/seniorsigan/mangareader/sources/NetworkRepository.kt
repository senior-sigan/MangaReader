package org.seniorsigan.mangareader.sources

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import okhttp3.*
import org.seniorsigan.mangareader.models.ChapterItem
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.models.PageItem
import org.seniorsigan.mangareader.sources.readmanga.ReadmangaMangaApiConverter
import org.seniorsigan.mangareader.sources.readmanga.Urls
import java.io.IOException
import java.net.URI

class NetworkRepository(
        private val urls: Urls,
        private val converter: ReadmangaMangaApiConverter,
        private val client: OkHttpClient = OkHttpClient()
) {

    fun getPopular(offset: Int = 0): Observable<MangaItem> {
        return wrap(urls.mangaList(offset)).map {
            converter.parseList(it, urls.base)
        }.flatMap {
            it.toObservable()
        }.concatMap {
            getMangaItem(it.url)
        }
    }

    fun getMangaItem(itemURL: URI): Observable<MangaItem> {
        return wrap(itemURL).map {
            converter.parseManga(it, urls.base)
        }
    }

    fun getPages(url: URI): Observable<List<PageItem>> {
        return wrap(url).map {
            converter.parseChapter(it)
        }
    }

    private fun wrap(uri: URI): Observable<String> = Observable.create { subscriber ->
        val req = Request.Builder().url(uri.toURL()).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                subscriber.onError(e)
            }

            override fun onResponse(call: Call, res: Response) {
                val html = res.body().string()
                subscriber.onNext(html)
                subscriber.onComplete()
            }
        })
    }
}