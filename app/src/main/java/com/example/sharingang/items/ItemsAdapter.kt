package com.example.sharingang.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.databinding.ItemViewBinding

class ItemsAdapter(private val clickListener: ItemListener, private val userId: String?) :
    ListAdapter<Item, ItemsAdapter.ItemViewHolder>(ItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener, userId)
    }

    class ItemViewHolder private constructor(private val binding: ItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, clickListener: ItemListener, userId: String?) {
            binding.itemListViewTitle.text = item.title
            binding.item = item
            binding.clickListener = clickListener
            //binding.isVisible = item.userId?.equals(userId) ?: false
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
    //val onEditListener: (item: Item) -> Unit,
    val onViewListener: (item: Item) -> Unit,
    //val onSellListener: (item: Item) -> Unit
) {
    //fun onEdit(item: Item) = onEditListener(item)
    fun onView(item: Item) = onViewListener(item)
    //fun onSell(item: Item) = onSellListener(item)
}