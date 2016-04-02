package org.seniorsigan.mangareader.ui

import android.app.SearchManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import org.jetbrains.anko.find
import org.jetbrains.anko.searchManager

import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG

class SearchActivity : AppCompatActivity() {
    lateinit var searchView: SearchView
    lateinit var scrim: View
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = find<SearchView>(R.id.search_view)
        scrim = find<View>(R.id.scrim)
        toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
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
        }
    }

    private fun clearResults() {
        //TODO: hide all views and clear adapters
    }

    private fun searchFor(query: String) {
        clearResults()
        searchView.clearFocus()
    }

    private fun setupSearchView() {
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.isIconified = false
        searchView.queryHint = getString(R.string.search_hint)
    }
}
