package com.example.sharingang.utils

import java.util.*

object DateHelper {

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
        val monthStr = getMonthName(date.month)
        return (
            when {
                today == messageTime -> "Today, ${date.hours}:${
                    date.minutes.toString().padStart(2, '0')
                }"
                today.first == messageTime.first -> "$monthStr ${date.day}"
                else -> "$monthStr ${date.day}, ${date.year}"
            }
        )
    }

    /**
     * get the name of the month based on its integer value in real life (i.e. 1 is equivalent
     * to January, not February)
     *
     * @param monthNumber the number of the month we want to get the name of
     * @return the name of the month
     */
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