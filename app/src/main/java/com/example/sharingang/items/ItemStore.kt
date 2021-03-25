package com.example.sharingang.items

/**
 * Represents a remote store of items, that we can access in some way.
 *
 * This isn't intended to be used directly in the UI (that's what an ItemRepository
 * would be for), but rather to provide an abstraction over the remote fetching of items.
 */
interface ItemStore {
    /**
     * Add a new item
     *
     * The item's id must be null.
     *
     * @return id of the item generated by the database
     */
    suspend fun addItem(item: Item): String?

    /**
     * Returns the item with corresponding id or null if it doesn't exist
     */
    suspend fun getItem(id: String): Item?

    /**
     * Returns all of the items that exist
     */
    suspend fun getAllItems(): List<Item>

    /**
     * Update existing item
     *
     * The item's id cannot be null.
     *
     * @return whether the update succeeded
     */
    suspend fun updateItem(item: Item): Boolean

    /**
     * Delete existing item
     *
     * @return true if the deletion succeeded or there is no item with such id
     */
    suspend fun deleteItem(id: String): Boolean
}