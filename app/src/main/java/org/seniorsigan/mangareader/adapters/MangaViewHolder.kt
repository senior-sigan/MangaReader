package org.seniorsigan.mangareader.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem

class MangaViewHolder(
        view: View
) : BaseItemHolder<MangaItem>(view) {
    val title = view.find<TextView>(R.id.manga_item_title)
    val cover = view.find<ImageView>(R.id.manga_item_cover)
    val context = view.context

    override fun setItem(item: MangaItem) {
        title.text = item.title
        Picasso.with(context).load(item.coverURL).into(cover)
        itemView.onClick {
            onItemClickListener.invoke(item)
        }
    }
}