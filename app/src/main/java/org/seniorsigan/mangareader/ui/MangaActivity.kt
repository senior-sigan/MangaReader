package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.INTENT_MANGA
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.ui.widgets.SimpleImageViewFacade

class MangaActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var button: Button
    private lateinit var description: TextView
    private lateinit var coverView: SimpleImageViewFacade

    private val searchEngine: String = "readmanga"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga)
        toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        collapsingToolbar = find<CollapsingToolbarLayout>(R.id.toolbar_layout)
        fab = find<FloatingActionButton>(R.id.fab)
        button = find<Button>(R.id.btn_manga_chapters)
        description = find<TextView>(R.id.manga_description)
        coverView = SimpleImageViewFacade(this, findViewById(R.id.manga_cover))

        val mangaIntent = intent.getSerializableExtra(INTENT_MANGA) as MangaItem?
        if (mangaIntent != null) {
            render(mangaIntent)
            App.mangaSearchController.find(searchEngine, mangaIntent.url, { response ->
                onUiThread {
                    render(response.data)
                }
            })
        }
    }

    fun render(manga: MangaItem?) {
        if (manga == null) return
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
}
