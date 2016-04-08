package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import okhttp3.HttpUrl
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.SHARED_URL
import org.seniorsigan.mangareader.TAG

class ShareParserActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_parser)

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
        var newIntent = Intent(this, MainActivity::class.java)
        App.pagesRepository.findAll(url.toString(), { pages ->
            if (pages.isNotEmpty()) {
                newIntent = Intent(this, ChapterActivity::class.java)
                newIntent.putExtra(SHARED_URL, pages.toTypedArray())
            }

            startActivity(newIntent)
            finish()
        })
    }
}