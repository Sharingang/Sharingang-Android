package com.example.sharingang.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.sharingang.items.Item
import java.util.Date

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

/**
 * User contains the public information about a user.
 *
 * It can be retrieved by anyone so it shouldn't store private data such as the user's favorite list.
 */
@Entity(indices = [Index(value = ["id"], unique = true)])
data class User(
    /** User ID received from the authentication provider */
    @ColumnInfo(name = "id")
    @DocumentId
    val id: String? = null,

    val name: String = "",

    /** URLs of the images */
    val profilePicture: String? = null,

    var wishlist: String = "",

    @ServerTimestamp
    val createdAt: Date? = null,

    /** ID used in local cache */
    @Exclude
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0
)
