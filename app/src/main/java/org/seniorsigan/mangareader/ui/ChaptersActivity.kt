package org.seniorsigan.mangareader.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.seniorsigan.mangareader.INTENT_MANGA_URL

import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.SHARED_URL
import org.seniorsigan.mangareader.ui.fragments.ChapterListFragment
import org.seniorsigan.mangareader.ui.fragments.MangaListFragment

class ChaptersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)

        val url = intent.getStringExtra(INTENT_MANGA_URL)
        if (savedInstanceState == null) {
            val chapterListFragment = ChapterListFragment()
            chapterListFragment.arguments = intent.extras
            chapterListFragment.arguments.putString(ChapterListFragment.urlArgument, url)
            supportFragmentManager.beginTransaction().add(R.id.fragments_container, chapterListFragment).commit()
        }
    }
}
