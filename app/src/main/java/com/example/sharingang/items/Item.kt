package com.example.sharingang.items

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
    val image: String = "",

    /** Provisory, will be changed later when we actually
        upload image to the server and cache it locally */
    var imageUri: String? = null,

    val price: Double = 0.0,

    val sold: Boolean = false,

    var category: Int = 0,
    var categoryString: String = "",

    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

    /** Id of the user that created the item */
    val userId: String? = null,

    var rated: Boolean = false,

    @ServerTimestamp
    val createdAt: Date? = null,

    /**
     * The ID we use locally
     */
    @Exclude
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0
) : Parcelable
