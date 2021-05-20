package com.example.sharingang.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Item represents an item available for sharing, or for sale.
 */
@Parcelize
@Entity(indices = [Index(value = ["id"], unique = true)])
data class Item(
    /** ID generated by the database */
    @ColumnInfo(name = "id")
    @DocumentId
    val id: String? = null,

    val title: String = "",

    val description: String = "",

    /** URL for an image*/
    val image: String? = null,

    val price: Double = 0.0,

    val discount: Boolean = false,
    val discountPrice: Double = 0.0,

    val sold: Boolean = false,

    var category: Int = 0,
    var categoryString: String = "",

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    /** Id of the user that created the item */
    val userId: String = "",

    val rated: Boolean = false,

    /** Where the item added is an offer (false), or a request*/
    val request: Boolean = false,
    
    @ServerTimestamp
    val createdAt: Date? = null,

    /**
     * The ID we use locally
     */
    @get:Exclude
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0
) : Parcelable
