package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import okhttp3.HttpUrl
import org.jetbrains.anko.find
import org.jetbrains.anko.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.adapters.ImagePageAdapter

class ChapterActivity : AppCompatActivity() {
    private lateinit var adapter: ImagePageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapter)

        val view = find<ViewPager>(R.id.chapter_view)
        adapter = ImagePageAdapter(applicationContext)
        view.adapter = adapter

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            parse(url)
        } else if (intent.data != null) {
            parse(intent.data.toString())
        } else {
            Log.d(TAG, "Nothing to do in ShareParserActivity")
            finish()
        }
    }

    fun parse(urlRaw: String) {
        val url = HttpUrl.parse(urlRaw)
        if (url == null) {
            Log.e(TAG, "Can't parse $urlRaw as url")
            finish()
            return
        }

        App.pagesRepository.findAll(url.toString(), { pages ->
            if (pages.isNotEmpty()) {
                onUiThread {
                    adapter.update(pages)
                }
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }
}
