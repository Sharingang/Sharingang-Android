package com.example.sharingang.items

import androidx.recyclerview.widget.DiffUtil

class ItemsDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Since it's a data class, the == checks if all the fields are equal
        // now there is only a description so it might work weirdly with same description items,
        // but later we'll check if their IDs are equal or not
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

}