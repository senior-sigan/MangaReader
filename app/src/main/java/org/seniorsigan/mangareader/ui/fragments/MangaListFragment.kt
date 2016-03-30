package org.seniorsigan.mangareader.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.async
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.adapters.MangaListAdapter

class MangaListFragment : Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private val adapter = MangaListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_manga_list, container, false)
        with(rootView, {
            refresh = find<SwipeRefreshLayout>(R.id.refresh_manga_list)
            listView = find<RecyclerView>(R.id.rv_manga_list)
        })

        listView.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = {manga ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(manga.url))
            startActivity(browserIntent)
        }
        listView.adapter = adapter
        refresh.onRefresh {
            renderList()
        }
        renderList()

        return rootView
    }

    fun renderList() {
        refresh.isRefreshing = true
        App.search.search { list ->
            onUiThread {
                adapter.insert(list)
                refresh.isRefreshing = false
            }
        }
    }
}
