package org.seniorsigan.mangareader.adapters

import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.ChapterItem

class ChapterViewHolder(
        view: View
) : BaseItemHolder<ChapterItem>(view) {
    val title = view.find<TextView>(R.id.chapter_item_title)
    val context = view.context

    override fun setItem(item: ChapterItem) {
        title.text = item.title
        itemView.onClick {
            onItemClickListener.invoke(item)
        }
    }
}