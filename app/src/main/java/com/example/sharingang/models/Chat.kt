package com.example.sharingang.models

import java.util.*

/**
 * Chat represents a message with additional metadata
 *
 * @property from the sender
 * @property to the receiver
 * @property message the actual message
 */
data class Chat(val from: String?, val to: String?, val message: String, val date: Date)
