package com.example.sharingang.utils

import java.util.*

object DateHelper {

    private val monthsMap = hashMapOf(
        0 to "Jan",
        1 to "Feb",
        2 to "Mar",
        3 to "Apr",
        4 to "May",
        5 to "Jun",
        6 to "Jul",
        7 to "Aug",
        8 to "Sep",
        9 to "Oct",
        10 to "Nov",
        11 to "Dec"
    )

    /**
     * formats a date for message timestamps
     *
     * @param date the date to format
     * @return the formatted date
     */
    fun formatMessageDate(date: Date): String {
        val currentDate = Date()
        val today = Triple(currentDate.year, currentDate.month, currentDate.day)
        val messageTime = Triple(date.year, date.month, date.day)
        val monthStr = monthsMap[date.month]
        return (
            when {
                today == messageTime -> "Today, ${date.hours}:${
                    date.minutes.toString().padStart(2, '0')
                }"
                today.first == messageTime.first -> "$monthStr. ${date.day}"
                else -> "$monthStr. ${date.day}, ${date.year}"
            }
        )
    }
}