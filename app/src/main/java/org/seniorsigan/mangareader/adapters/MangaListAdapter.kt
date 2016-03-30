package org.seniorsigan.mangareader.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.onClick
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem

class MangaListAdapter: RecyclerView.Adapter<MangaViewHolder>() {
    private val collection: MutableList<MangaItem> = arrayListOf()
    var onItemClickListener: ((MangaItem) -> Unit)? = null

    fun insert(models: List<MangaItem>) {
        collection.addAll(models)
        notifyDataSetChanged()
    }

    fun insert(model: MangaItem) {
        collection.add(model)
        notifyItemInserted(collection.indexOfFirst { it._id == model._id })
    }

    override fun onBindViewHolder(holder: MangaViewHolder?, position: Int) {
        val item = collection.getOrNull(position) ?: return
        holder?.setItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder? {
        val view = parent.context.layoutInflater.inflate(R.layout.manga_item, parent, false)
        return MangaViewHolder(view, onItemClickListener)
    }

    override fun getItemCount(): Int = collection.size
}

class MangaViewHolder(val view: View, private val onItemClickListener: ((MangaItem) -> Unit)?): RecyclerView.ViewHolder(view) {
    val title = view.find<TextView>(R.id.manga_item_title)
    val cover = view.find<ImageView>(R.id.manga_item_cover)
    val context = view.context

    fun setItem(item: MangaItem) {
        title.text = item.title
        Picasso.with(context).load(item.coverURL).into(cover)
        itemView.onClick {
            onItemClickListener?.invoke(item)
        }
    }
}