package org.seniorsigan.mangareader.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.onUiThread
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.ShareParserActivity
import org.seniorsigan.mangareader.adapters.ArrayListAdapter
import org.seniorsigan.mangareader.adapters.ChapterViewHolder

class ChapterListFragment : Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private val adapter = ArrayListAdapter(ChapterViewHolder::class.java, R.layout.chapter_item)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_chapter_list, container, false)
        with(rootView, {
            refresh = find<SwipeRefreshLayout>(R.id.refresh_chapter_list)
            listView = find<RecyclerView>(R.id.rv_chapter_list)
        })
        val url = "http://readmanga.me/naruto"

        listView.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { chapter ->
            startActivity(with(Intent(context, ShareParserActivity::class.java), {
                putExtra(android.content.Intent.EXTRA_TEXT, chapter.url)
            }))
        }
        listView.adapter = adapter
        refresh.onRefresh {
            renderList(url)
        }
        renderList(url)

        return rootView
    }

    fun renderList(url: String) {
        refresh.isRefreshing = true
        App.chaptersRepository.findAll(url, { list ->
            onUiThread {
                adapter.insert(list)
                refresh.isRefreshing = false
            }
        })
    }
}