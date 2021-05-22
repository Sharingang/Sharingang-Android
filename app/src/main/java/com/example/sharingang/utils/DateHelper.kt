package com.example.sharingang.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {
    fun getDateDifferenceInDays(startDate: Date, endDate: Date): Long {
        return TimeUnit.DAYS.convert(endDate.time - startDate.time,
            TimeUnit.MILLISECONDS)
    }

    fun formatMessageDate(date: Date): String {
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