package org.seniorsigan.mangareader.ui

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import org.jetbrains.anko.find
import org.jetbrains.anko.onUiThread
import org.jetbrains.anko.searchManager
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.INTENT_MANGA
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.adapters.ArrayListAdapter
import org.seniorsigan.mangareader.adapters.MangaViewHolder

class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var scrim: View
    lateinit var toolbar: Toolbar
    lateinit var listView: RecyclerView
    lateinit var progress: ProgressBar
    private val adapter = ArrayListAdapter(MangaViewHolder::class.java, R.layout.manga_item)

    private val searchEngine: String = "readmanga"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        listView = find<RecyclerView>(R.id.rv_manga_list)
        searchView = find<SearchView>(R.id.search_view)
        scrim = find<View>(R.id.scrim)
        toolbar = find<Toolbar>(R.id.toolbar)
        progress = find<ProgressBar>(R.id.progressBar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        listView.layoutManager = LinearLayoutManager(applicationContext)
        adapter.onItemClickListener = { manga ->
            startActivity(with(Intent(this, MangaActivity::class.java), {
                putExtra(INTENT_MANGA, manga)
                this
            }))
        }
        listView.adapter = adapter

        setupSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.hasExtra(SearchManager.QUERY)) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: ""
            if (query.isNotEmpty()) {
                searchView.setQuery(query, false)
                searchFor(query)
            }
        } else {
            clearResults()
        }
    }

    private fun clearResults() {
        progress.visibility = View.GONE
        adapter.clear()
    }

    private fun searchFor(query: String) {
        clearResults()
        progress.visibility = View.VISIBLE
        searchView.clearFocus()
        App.mangaSearchController.search(searchEngine, query, { list ->
            Log.d(TAG, "Found $list")
            onUiThread {
                progress.visibility = View.GONE
                adapter.insert(list)
            }
        })
    }

    private fun setupSearchView() {
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isIconified = false
        searchView.queryHint = getString(R.string.search_hint)
    }
}
