package org.seniorsigan.mangareader.adapters

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.FeedItem

class FeedViewHolder(
        view: View
) : BaseItemHolder<FeedItem>(view) {
    val title = view.find<TextView>(R.id.feed_item_title)
    val context = view.context

    override fun setItem(item: FeedItem) {
        title.text = item.chapter.title
        title.typeface = if (!item.isRead) {
            Typeface.DEFAULT_BOLD
        } else {
            Typeface.DEFAULT
        }
        itemView.onClick {
            onItemClickListener.invoke(item)
        }
    }
}