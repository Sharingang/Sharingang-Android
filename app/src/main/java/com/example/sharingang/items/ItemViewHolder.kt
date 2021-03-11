package com.example.sharingang.items

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.sharingang.databinding.ItemViewBinding

class ItemViewHolder private constructor(val binding: ItemViewBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val editButton: Button = binding.itemListViewEditBtn

    fun bind(item: Item, clickListener: ItemListener) {
        binding.itemListViewTitle.text = item.description
        binding.item = item
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