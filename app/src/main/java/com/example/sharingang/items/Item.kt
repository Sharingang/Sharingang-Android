package com.example.sharingang.items

import java.util.*

/**
 * Item represents an item available for sharing, or for sale.
 */
data class Item(
    val title: String = "",

    val description: String = "",

    /** URLs of the images */
    val images: List<String> = listOf(),

    /** Price in cents */
    val price: Int = 0,

    val createdAt: Date = Date(),

    var id: String? = null
)
