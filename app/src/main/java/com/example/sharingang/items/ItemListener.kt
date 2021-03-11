package com.example.sharingang.items

// Replace the description with its ID once the database is set
class ItemListener(val clickListener: (item: Item) -> Unit) {
    fun onClick(item: Item) = clickListener(item)
}