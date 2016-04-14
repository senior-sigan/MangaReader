package org.seniorsigan.mangareader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.adapters.ArrayListAdapter
import org.seniorsigan.mangareader.adapters.MangaViewHolder
import org.seniorsigan.mangareader.models.MangaItem

class BookmarkListFragment : Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ArrayListAdapter(MangaViewHolder::class.java, R.layout.manga_item)

    lateinit var onItemClickListener: OnItemClickListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            onItemClickListener = activity as OnItemClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement OnItemClickListener");
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_manga_list, container, false)
        with(rootView, {
            refresh = find<SwipeRefreshLayout>(R.id.refresh_manga_list)
            listView = find<RecyclerView>(R.id.rv_manga_list)
            progressBar = find<ProgressBar>(R.id.progressBar)
        })

        listView.layoutManager = GridLayoutManager(context, 2)
        adapter.onItemClickListener = { manga -> onItemClickListener.onItemClick(manga) }
        listView.adapter = adapter

        return rootView
    }

    override fun onStart() {
        super.onStart()
        refresh.onRefresh {
            renderList()
        }
        renderList()
    }

    fun renderList() {
        App.bookmarkManager.search({ list ->
            if (activity == null) return@search
            onUiThread {
                adapter.update(list)
                refresh.isRefreshing = false
                progressBar.visibility = View.GONE
            }
        })
    }

    interface OnItemClickListener {
        fun onItemClick(item: MangaItem)
    }
}
