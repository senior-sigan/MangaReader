package org.seniorsigan.mangareader

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaParser

class ShareParserActivity: AppCompatActivity() {
    val parser = ReadmangaParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var newIntent = Intent(this, MainActivity::class.java)

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            parser.extractPages(url, { pages ->
                Log.d(TAG, "ShareParserActivity for $url find $pages")
                if (pages.isNotEmpty()) {
                    newIntent = Intent(this, ChapterActivity::class.java)
                    newIntent.putExtra(SHARED_URL, pages.toTypedArray())
                }

                startActivity(newIntent)
                finish()
            })
        }
    }
}