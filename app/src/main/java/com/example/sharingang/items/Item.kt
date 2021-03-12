package com.example.sharingang.items

import java.util.Date

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Item represents an item available for sharing, or for sale.
 */
@Parcelize
data class Item(
    val title: String = "",

    val description: String = "",

    /** URLs of the images */
    val images: List<String> = listOf(),

    /** Price in cents */
    val price: Int = 0,

    val createdAt: Date = Date(),

    /** ID generated by the database */
    var id: String? = null
)
