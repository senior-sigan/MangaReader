package org.seniorsigan.mangareader.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.seniorsigan.mangareader.INTENT_MANGA
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.ui.fragments.ChapterListFragment

class ChaptersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chapters)

        val manga = intent.getSerializableExtra(INTENT_MANGA) as MangaItem?
        if (savedInstanceState == null) {
            val chapterListFragment = ChapterListFragment()
            chapterListFragment.arguments = intent.extras
            chapterListFragment.arguments.putSerializable(ChapterListFragment.mangaItemArgument, manga)
            supportFragmentManager.beginTransaction().add(R.id.fragments_container, chapterListFragment).commit()
        }
    }
}
