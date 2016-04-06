package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.INTENT_MANGA
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.ui.widgets.SimpleImageViewFacade

class MangaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val collapsingToolbar = find<CollapsingToolbarLayout>(R.id.toolbar_layout)

        val fab = find<FloatingActionButton>(R.id.fab)

        val button = find<Button>(R.id.btn_manga_chapters)

        val description = find<TextView>(R.id.manga_description)
        val coverView = SimpleImageViewFacade(this, findViewById(R.id.manga_cover))
        val mangaIntent = intent.getSerializableExtra(INTENT_MANGA) as MangaItem?
        if (mangaIntent != null) {
            App.mangaPageParser.parse(mangaIntent.url, { manga ->
                Log.d(TAG, "MangaActivity $manga")
                onUiThread {
                    if (manga == null) return@onUiThread
                    description.text = manga.description
                    supportActionBar?.title = manga.title
                    collapsingToolbar.title = manga.title
                    coverView.load(manga.coverURL)
                    button.onClick {
                        startActivity(with(Intent(this, ChaptersActivity::class.java), {
                            putExtra(INTENT_MANGA, manga)
                            this
                        }))
                    }
                    fab.onClick { view ->
                        App.bookmarkManager.save(manga)
                        Snackbar.make(view!!, "${manga.title} saved in bookmarks", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                    }
                }
            })
        }
    }
}
