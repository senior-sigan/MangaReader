package org.seniorsigan.mangareader.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.layoutInflater
import org.seniorsigan.mangareader.models.BaseItem

class ArrayListAdapter<Item: BaseItem, ItemHolder: BaseItemHolder<Item>>(
        private val itemHolderClass: Class<ItemHolder>,
        private val itemLayout: Int
): RecyclerView.Adapter<ItemHolder>() {
    private val collection: MutableList<Item> = arrayListOf()
    var onItemClickListener: ((Item) -> Unit)? = null

    fun insert(models: List<Item>) {
        collection.addAll(models)
        notifyDataSetChanged()
    }

    fun insert(model: Item) {
        collection.add(model)
        notifyItemInserted(collection.indexOfFirst { it._id == model._id })
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = collection.getOrNull(position) ?: return
        holder.setItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder? {
        val view = parent.context.layoutInflater.inflate(itemLayout, parent, false)
        return itemHolderClass.constructors[0].newInstance(view, onItemClickListener) as ItemHolder
    }

    override fun getItemCount(): Int = collection.size
}

abstract class BaseItemHolder<Item: BaseItem>(
        val view: View, private val onItemClickListener: ((Item) -> Unit)?
) : RecyclerView.ViewHolder(view) {
    abstract fun setItem(item: Item)
}