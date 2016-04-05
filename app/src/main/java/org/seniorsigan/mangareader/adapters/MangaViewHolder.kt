package org.seniorsigan.mangareader.adapters

import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.ui.widgets.SimpleImageViewFacade

class MangaViewHolder(
        view: View
) : BaseItemHolder<MangaItem>(view) {
    val title = view.find<TextView>(R.id.manga_item_title)
    val context = view.context
    val cover = SimpleImageViewFacade(context).attach(view, R.id.manga_item_cover)

    override fun setItem(item: MangaItem) {
        title.text = item.title
        cover.load(item.coverURL)
        itemView.onClick {
            onItemClickListener.invoke(item)
        }
    }
}