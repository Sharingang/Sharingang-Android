package com.example.sharingang

/**
 * Chat represents a message with additional metadata
 *
 * @param from the sender
 * @param to the receiver
 * @param message the actual message
 */
class Chat constructor(val from: String?, val to: String?, val message: String)