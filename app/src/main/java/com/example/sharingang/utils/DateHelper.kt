package com.example.sharingang.utils

import android.content.Context
import com.example.sharingang.R
import java.util.*

/**
 * Helper for date calculations and date/time formatting
 * @property context the context
 */
class DateHelper(private val context: Context) {

    private val monthsMap = hashMapOf(
        0 to context.resources.getString(R.string.eng_jan),
        1 to context.resources.getString(R.string.eng_feb),
        2 to context.resources.getString(R.string.eng_mar),
        3 to context.resources.getString(R.string.eng_apr),
        4 to context.resources.getString(R.string.eng_may),
        5 to context.resources.getString(R.string.eng_jun),
        6 to context.resources.getString(R.string.eng_jul),
        7 to context.resources.getString(R.string.eng_aug),
        8 to context.resources.getString(R.string.eng_sep),
        9 to context.resources.getString(R.string.eng_oct),
        10 to context.resources.getString(R.string.eng_nov),
        11 to context.resources.getString(R.string.eng_dec),
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
                today == messageTime -> context.getString(R.string.eng_today,
                    ", ${date.hours}:${date.minutes.toString()
                        .padStart(2, '0')}"
                )
                today.first == messageTime.first -> "$monthStr. ${date.day}"
                else -> "$monthStr. ${date.day}, ${date.year}"
            }
        )
    }
}