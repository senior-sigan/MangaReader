package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var description: TextView
    private lateinit var coverView: SimpleImageViewFacade
    private var renderBookmarkMenu: (MangaItem) -> Unit = {}
    private var saveOrRemove: () -> Unit = {}

    private val searchEngine: String = "readmanga"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manga)
        toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        collapsingToolbar = find<CollapsingToolbarLayout>(R.id.toolbar_layout)
        fab = find<FloatingActionButton>(R.id.fab)
        description = find<TextView>(R.id.manga_description)
        coverView = SimpleImageViewFacade(this, findViewById(R.id.manga_cover))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_manga, menu)
        renderBookmarkMenu = renderBookmarkMenuLazy(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_add_bookmark -> {
                saveOrRemove()
                true
            }
            R.id.action_remove_bookmark -> {
                saveOrRemove()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderBookmarkMenuLazy(menu: Menu) = { manga: MangaItem ->
        App.bookmarkManager.find(manga, { bookmark ->
            if (bookmark == null) {
                menu.findItem(R.id.action_add_bookmark).isVisible = true
                menu.findItem(R.id.action_remove_bookmark).isVisible = false
            } else {
                menu.findItem(R.id.action_add_bookmark).isVisible = false
                menu.findItem(R.id.action_remove_bookmark).isVisible = true
            }
        })
    }

    private fun saveOrRemoveLazy(manga: MangaItem) = {
        App.bookmarkManager.saveOrRemove(manga)
        renderBookmarkMenu(manga)
    }

    private fun render(manga: MangaItem?) {
        if (manga == null) return
        saveOrRemove = saveOrRemoveLazy(manga)
        description.text = manga.description
        supportActionBar?.title = manga.title
        collapsingToolbar.title = manga.title
        coverView.load(manga.coverURL)
        renderBookmarkMenu(manga)
        fab.onClick { view ->
            startActivity(with(Intent(this, ChaptersActivity::class.java), {
                putExtra(INTENT_MANGA, manga)
                this
            }))
        }
    }
}
