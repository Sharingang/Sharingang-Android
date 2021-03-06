package com.example.sharingang.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Offer(
    var title: String? = null,
    var description: String? = null,
    @ServerTimestamp var timestamp: Date? = null
) {
    constructor(title: String, description: String) : this() {
        this.title = title
        this.description = description
    }
}
