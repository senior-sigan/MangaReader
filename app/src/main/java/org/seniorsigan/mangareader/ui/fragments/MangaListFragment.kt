package org.seniorsigan.mangareader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.TAG
import org.seniorsigan.mangareader.adapters.ArrayListAdapter
import org.seniorsigan.mangareader.adapters.MangaViewHolder
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.usecases.readmanga.ReadmangaSearch

class MangaListFragment : Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ArrayListAdapter(MangaViewHolder::class.java, R.layout.manga_item)
    private var currentEngine: String? = null

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
        currentEngine = savedInstanceState?.getString(searchArgument)

        listView.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { manga -> onItemClickListener.onItemClick(manga) }
        listView.adapter = adapter

        return rootView
    }

    override fun onStart() {
        super.onStart()
        if (arguments != null) {
            currentEngine = arguments.getString(searchArgument)
        }
        refresh.onRefresh {
            renderList()
        }
        renderList()
    }

    fun renderList() {
        if (currentEngine != null) {
            App.searchController.search(currentEngine!!, { list ->
                if (activity == null) return@search
                Log.d(TAG, "Bookmarks $list")
                onUiThread {
                    adapter.update(list)
                    refresh.isRefreshing = false
                    progressBar.visibility = View.GONE
                }
            })
        } else {
            refresh.isRefreshing = false
            progressBar.visibility = View.GONE
            Log.d(TAG, "Nothing to draw because engine wasn't selected")
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: MangaItem)
    }

    companion object {
        val searchArgument = "SEARCH_ENGINE_ARGUMENT"
    }
}
