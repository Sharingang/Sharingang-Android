package com.example.sharingang.items

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Item represents an item available for sharing, or for sale.
 */
@Parcelize
data class Item(val description: String) : Parcelable
