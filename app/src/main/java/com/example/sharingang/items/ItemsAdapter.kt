package com.example.sharingang.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sharingang.databinding.ItemViewBinding

/**
 * Adapter to display items in a recycler view.
 * @property listener an item listener
 */
class ItemsAdapter(private val clickListener: ItemListener) :
    ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    /**
     * Recycler view to represent a single item.
     * @property binding item view binding
     */
    class ItemViewHolder private constructor(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, clickListener: ItemListener) {
            binding.itemListViewTitle.text = item.title
            binding.item = item
            Glide.with(binding.itemImagePreview).load(item.image).into(binding.itemImagePreview)
            binding.clickListener = clickListener
        }

        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemViewBinding.inflate(layoutInflater, parent, false)
                return ItemViewHolder(binding)
            }
        }
    }
}

class ItemsDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}

// Replace the description with its ID once the database is set
class ItemListener(
    val onViewListener: (item: Item) -> Unit,
) {
    fun onView(item: Item) = onViewListener(item)
}
