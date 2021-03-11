package com.example.sharingang.items

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class ItemsAdapter(private val clickListener: ItemListener) :
    ListAdapter<Item, ItemViewHolder>(ItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}