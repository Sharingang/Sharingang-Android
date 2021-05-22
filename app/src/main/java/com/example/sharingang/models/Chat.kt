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
                when {
                    today == messageTime -> "Today, ${date.hours}:${
                        if(date.minutes < 10) "0" + "${date.minutes}"
                        else date.minutes
                    }"
                    today.first == messageTime.first -> "$monthStr ${date.day}"
                    else -> "$monthStr ${date.day}, ${date.year}"
                }
                )
    }
    private fun getMonthName(monthNumber: Int): String {
        return (
                when(monthNumber) {
                    1 -> "Jan."
                    2 -> "Feb."
                    3 -> "Mar."
                    4 -> "Apr."
                    5 -> "May."
                    6 -> "Jun."
                    7 -> "Jul."
                    8 -> "Aug."
                    9 -> "Sep."
                    10 -> "Oct."
                    11 -> "Nov."
                    12 -> "Dec."
                    else -> ""
                }
                )
    }

}
