package org.seniorsigan.mangareader.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
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
import org.seniorsigan.mangareader.adapters.FeedViewHolder
import org.seniorsigan.mangareader.ui.ChapterActivity


class FeedListFragment: Fragment() {
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adapter = ArrayListAdapter(FeedViewHolder::class.java, R.layout.feed_item)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_feed_list, container, false)
        with(rootView, {
            refresh = find<SwipeRefreshLayout>(R.id.refresh_feed_list)
            listView = find<RecyclerView>(R.id.rv_feed_list)
            progressBar = find<ProgressBar>(R.id.progressBar)
        })

        listView.layoutManager = LinearLayoutManager(context)
        adapter.onItemClickListener = { feedItem ->
            startActivity(with(Intent(context, ChapterActivity::class.java), {
                putExtra(android.content.Intent.EXTRA_TEXT, feedItem.chapter.url)
            }))
            App.updatesNotification.markRead(feedItem.chapter)
            App.feedRepository.markRead(feedItem)
        }
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
        App.feedRepository.findAll({ response ->
            if (activity == null) return@findAll

            onUiThread {
                if (response.error == null) {
                    adapter.update(response.data)
                    refresh.isRefreshing = false
                    progressBar.visibility = View.GONE
                } else {
                    Log.e(TAG, response.error)
                }
            }
        })
    }
}