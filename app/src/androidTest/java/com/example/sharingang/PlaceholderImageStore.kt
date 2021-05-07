package com.example.sharingang

import android.net.Uri
import com.example.sharingang.users.CurrentUserProvider
import javax.inject.Inject

class PlaceholderImageStore @Inject constructor(
    private val currentUserProvider: CurrentUserProvider
) : ImageStore {

    private val placeholderImages = listOf(
        "https://loremflickr.com/240/240/dog",
        "https://loremflickr.com/240/240/naruto",
        "https://loremflickr.com/240/240/manga",
        "https://loremflickr.com/240/240/tv",
        "https://loremflickr.com/240/240/book",
        "https://loremflickr.com/240/240/videogame"
    )

    override suspend fun store(imageUri: Uri): Uri? {
        if (currentUserProvider.getCurrentUserId() == null) {
            return null
        }

        return Uri.parse(placeholderImages.random())
    }
}