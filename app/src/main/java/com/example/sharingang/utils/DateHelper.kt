package com.example.sharingang.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper for date calculations and time formatting
 */
object DateHelper {

    /**
     * Contains units of measure for time
     */
    enum class Measure {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
    }

    private val unitStringMap = hashMapOf (
        Measure.SECONDS to "s",
        Measure.MINUTES to "min",
        Measure.HOURS to "h",
        Measure.DAYS to "d",
        Measure.WEEKS to "w",
        Measure.MONTHS to "mon",
        Measure.YEARS to "y"
    )

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


    /**
     * Gives us the formatted difference between two dates depending on how far the two dates
     * are from each other. Close dates will give us a result in seconds or minutes or hours,
     * and dates that are far from each other will give us one in days, weeks, months or years.
     *
     * @param startDate the date from which we start counting
     * @param endDate the date at which we stop counting
     * @return the formatted difference between the two dates
     */
    fun getDateDifferenceString(startDate: Date, endDate: Date): String {
        val end = endDate.time
        val start = startDate.time
        val timeDiff = end - start
        val diffInSeconds = TimeUnit.SECONDS.convert(timeDiff,
            TimeUnit.MILLISECONDS)
        val lessThanADay = diffInSeconds < SECS_PER_DAY
        val accurateDiff =
            if(lessThanADay) getDiffFromSeconds(diffInSeconds)
            else getDiffFromDays(TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS))

        return getFormattedDifference(accurateDiff)
    }

    /**
     * Gives us an accurate difference based on the time difference in seconds
     *
     * @param diffInSeconds the difference in seconds
     * @return a pair containing the difference number and its unit of measure
     */
    private fun getDiffFromSeconds(diffInSeconds: Long): Pair<Long, Measure> {
        return (
            when {
                diffInSeconds < SECS_PER_MINUTE -> Pair(diffInSeconds, Measure.SECONDS)
                diffInSeconds < SECS_PER_HOUR -> Pair(diffInSeconds / SECS_PER_MINUTE, Measure.MINUTES)
                else -> Pair(diffInSeconds / SECS_PER_HOUR, Measure.HOURS)
            }
        )
    }

    /**
     * Gives us an accurate difference based on the time difference in days
     *
     * @param diffInDays the difference in days
     * @return a pair containing the difference number and its unit of measure
     */
    private fun getDiffFromDays(diffInDays: Long): Pair<Long, Measure> {
        return (
            when {
                diffInDays < DAYS_PER_WEEK -> Pair(diffInDays, Measure.DAYS)
                diffInDays < DAYS_PER_MONTH -> Pair(diffInDays / DAYS_PER_WEEK, Measure.WEEKS)
                diffInDays < DAYS_PER_YEAR -> Pair(diffInDays / DAYS_PER_MONTH, Measure.MONTHS)
                else -> Pair(diffInDays / DAYS_PER_YEAR, Measure.YEARS)
            }
        )
    }

    /**
     * Gives us a formatted time based on a number and its unit of measure
     *
     * @param time a pair containing the number and its unit of measure
     * @return the formatted result
     */
    private fun getFormattedDifference(time: Pair<Long, Measure>): String {
        val diffNumber = time.first
        val measureStr = unitStringMap[time.second]
        return "$diffNumber$measureStr"
    }

    /**
     * Creates a real Date from a formatted date and time
     *
     * @param dateStr date in format "yyyy/MM/dd HH:mm:ss"
     * @return the created Date
     */
    @SuppressLint("SimpleDateFormat")
    fun createDate(dateStr: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return try {
            dateFormat.parse(dateStr)
        } catch (e: ParseException) {
            null
        }

    }

}