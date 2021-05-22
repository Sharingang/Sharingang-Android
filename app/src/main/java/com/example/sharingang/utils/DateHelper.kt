package com.example.sharingang.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {

    enum class Measure {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
    }

    /*
     * Note: It is true that days can differ between years and months. Here, we are only
     * concerned by an estimation of the time between two dates, which is why this very simplified
     * implementation will do (as it is used for the last time an item was updated for example,
     * which does not require a very precise estimation of time.)
     */
    private const val SECS_PER_MINUTE = 60L
    private const val SECS_PER_HOUR = 3600L
    private const val SECS_PER_DAY = 86400L
    private const val DAYS_PER_WEEK = 7L
    private const val DAYS_PER_MONTH = 30L
    private const val DAYS_PER_YEAR = 365L


    fun getDateDifferenceString(startDate: Date, endDate: Date): String {
        val endTime = endDate.time
        val startTime = startDate.time
        val diffInSeconds = TimeUnit.SECONDS.convert(endTime - startTime,
            TimeUnit.MILLISECONDS)
        val lessThanADay = diffInSeconds < SECS_PER_DAY
        val accurateDiff =
            if(lessThanADay) getAccurateDiffFromSeconds(diffInSeconds)
            else {
                val diffInDays =
                    TimeUnit.DAYS.convert(endTime - startTime, TimeUnit.MILLISECONDS)
                getAccurateDiffFromDays(diffInDays)
            }
        return getFormattedDifference(accurateDiff)
    }

    private fun getAccurateDiffFromSeconds(diffInSeconds: Long): Pair<Long, Measure> {
        return (
            when {
                diffInSeconds < SECS_PER_MINUTE -> Pair(diffInSeconds, Measure.SECONDS)
                diffInSeconds < SECS_PER_HOUR -> Pair(diffInSeconds / SECS_PER_MINUTE, Measure.MINUTES)
                else -> Pair(diffInSeconds / SECS_PER_DAY, Measure.HOURS)
            }
        )
    }

    private fun getAccurateDiffFromDays(diffInDays: Long): Pair<Long, Measure> {
        return (
            when {
                diffInDays < DAYS_PER_WEEK -> Pair(diffInDays, Measure.DAYS)
                diffInDays < DAYS_PER_MONTH -> Pair(diffInDays / DAYS_PER_WEEK, Measure.WEEKS)
                diffInDays < DAYS_PER_YEAR -> Pair(diffInDays / DAYS_PER_MONTH, Measure.MONTHS)
                else -> Pair(diffInDays / DAYS_PER_YEAR, Measure.YEARS)
            }
        )
    }

    private fun getFormattedDifference(difference: Pair<Long, Measure>): String {
        val diffNumber = difference.first
        val measureStr =
            when(difference.second) {
                Measure.SECONDS -> "s"
                Measure.MINUTES -> "min"
                Measure.HOURS -> "h"
                Measure.DAYS -> "d"
                Measure.WEEKS -> "w"
                Measure.MONTHS -> "mon"
                Measure.YEARS -> "y"
            }
        return "$diffNumber$measureStr"
    }

}