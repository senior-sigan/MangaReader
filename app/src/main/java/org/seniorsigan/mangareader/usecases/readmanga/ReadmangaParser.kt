package org.seniorsigan.mangareader.usecases.readmanga

import android.net.Uri
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.TAG
import java.io.IOException
import java.util.regex.Pattern

class ReadmangaParser {
    private val regexp = "\\[\\[(.*?)\\]\\]"

    fun extractPages(url: String, callback: (List<String>) -> Unit) {
        val uri = Uri.parse(url).buildUpon().appendQueryParameter("mature", "1")
        val req = Request.Builder().url(uri.toString()).build()
        App.client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.e(TAG, "Can't finish request to $url: $e")
            }

            override fun onResponse(call: Call?, response: Response?) {
                val html = response?.body()?.string()
                val data = extract(html)
                if (data == null) {
                    callback(emptyList())
                    return
                }
                val pages = arrayListOf<Page>()
                with(JSONArray(data), {
                    for (i in 0..length()-1) {
                        val page = getJSONArray(i)
                        pages.add(Page(
                                host = page.getString(1),
                                path = page.getString(0),
                                item = page.getString(2)
                        ))
                    }
                })

                callback(pages.map { it.uri })
            }
        })
    }

    private fun extract(str: String?): String? {
        if (str == null) return null
        val pattern = Pattern.compile(regexp)
        val matcher = pattern.matcher(str)
        if (matcher.find()) return matcher.group(0)
        return null
    }
}

private data class Page(
        val host: String,
        val path: String,
        val item: String
) {
    val uri: String
        get() = "$host$path$item"
}