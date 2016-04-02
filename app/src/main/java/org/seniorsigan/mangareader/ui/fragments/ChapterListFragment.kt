package org.seniorsigan.mangareader.ui.fragments

import android.content.Intent
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
import org.seniorsigan.mangareader.adapters.ChapterViewHolder
import org.seniorsigan.mangareader.ui.ShareParserActivity

class ChapterListFragment : Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ArrayListAdapter(ChapterViewHolder::class.java, R.layout.chapter_item)
    private var currentURL: String? = null

    companion object {
        val urlArgument = "CHAPTER_URL_ARGUMENT"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_chapter_list, container, false)
        with(rootView, {
            refresh = find<SwipeRefreshLayout>(R.id.refresh_chapter_list)
            listView = find<RecyclerView>(R.id.rv_chapter_list)
            progressBar = find<ProgressBar>(R.id.progressBar)
        })
        currentURL = savedInstanceState?.getString(urlArgument)

        listView.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { chapter ->
            startActivity(with(Intent(context, ShareParserActivity::class.java), {
                putExtra(android.content.Intent.EXTRA_TEXT, chapter.url)
            }))
        }
        listView.adapter = adapter

        return rootView
    }

    override fun onStart() {
        super.onStart()
        if (arguments != null) {
            currentURL = arguments.getString(urlArgument)
        }
        refresh.onRefresh {
            renderList(currentURL)
        }
        renderList(currentURL)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(urlArgument, currentURL)
    }

    fun renderList(url: String?) {
        if (url == null) {
            Log.w(TAG, "ChapterListFragment get null url. Nothing to show.")
            refresh.isRefreshing = false
            return
        }

        App.chaptersRepository.findAll(url, { list ->
            Log.d(TAG, "Find chapters for $url")
            if (activity == null) return@findAll
            onUiThread {
                adapter.insert(list)
                refresh.isRefreshing = false
                progressBar.visibility = View.GONE
            }
        })
    }
}
