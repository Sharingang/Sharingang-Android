package com.example.sharingang.models

import java.util.*

/**
 * Chat represents a message with additional metadata
 *
 * @property from the sender
 * @property to the receiver
 * @property message the actual message
 */
data class Chat(val from: String?, val to: String?, val message: String, val date: Date) {
    fun getMessageTime(): String {
        val currentDate = Date()
        val today = Triple(currentDate.year, currentDate.month, currentDate.day)
        val messageTime = Triple(date.year, date.month, date.day)
        val monthStr = getMonthName(date.month)
        return (
                if(today == messageTime) "Today, ${date.hours}:${date.minutes}"
                else if (today.first == messageTime.first) "$monthStr ${date.day}"
                else "$monthStr ${date.day}, ${date.year}"
                )
    }
    private fun getMonthName(monthNumber: Int): String {
        return (
                when(monthNumber) {
                    1 -> "January"
                    2 -> "February"
                    3 -> "March"
                    4 -> "April"
                    5 -> "May"
                    6 -> "June"
                    7 -> "July"
                    8 -> "August"
                    9 -> "September"
                    10 -> "October"
                    11 -> "November"
                    12 -> "December"
                    else -> ""
                }
                )
    }

}
