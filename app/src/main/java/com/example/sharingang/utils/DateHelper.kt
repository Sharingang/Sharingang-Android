package com.example.sharingang.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.sharingang.R
import java.util.*
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat



/**
 * Helper for date calculations and date/time formatting
 * @property context the context
 */
class DateHelper(private val context: Context?) {

    /*
     * Note: It is true that days can differ between years and months. Here, we are only
     * concerned by an estimation of the time between two dates, which is why this very simplified
     * implementation will do (as it is used for the last time an item was updated for example,
     * which does not require a very precise estimation of time.)
     */
    private val SECS_PER_MINUTE = 60L
    private val SECS_PER_HOUR = 3600L
    private val SECS_PER_DAY = 86400L
    private val DAYS_PER_WEEK = 7L
    private val DAYS_PER_MONTH = 30L
    private val DAYS_PER_YEAR = 365L


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

    private val monthsMap = hashMapOf(
        0 to (context?.getString(R.string.eng_jan) ?: "Jan"),
        1 to (context?.getString(R.string.eng_feb) ?: "Feb"),
        2 to (context?.getString(R.string.eng_mar) ?: "Mar"),
        3 to (context?.getString(R.string.eng_apr) ?: "Apr"),
        4 to (context?.getString(R.string.eng_may) ?: "May"),
        5 to (context?.getString(R.string.eng_jun) ?: "Jun"),
        6 to (context?.getString(R.string.eng_jul) ?: "Jul"),
        7 to (context?.getString(R.string.eng_aug) ?: "Aug"),
        8 to (context?.getString(R.string.eng_sep) ?: "Sep"),
        9 to (context?.getString(R.string.eng_oct) ?: "Oct"),
        10 to (context?.getString(R.string.eng_nov) ?: "Nov"),
        11 to (context?.getString(R.string.eng_dec) ?: "Dec")
    )


    private var unitStringMap: HashMap<Measure, String>? =
        hashMapOf(
            Measure.SECONDS to (context?.getString(R.string.eng_seconds) ?: "s"),
            Measure.MINUTES to (context?.getString(R.string.eng_minutes) ?: "min"),
            Measure.HOURS to (context?.getString(R.string.eng_hours) ?: "h"),
            Measure.DAYS to (context?.getString(R.string.eng_days) ?: "d"),
            Measure.WEEKS to (context?.getString(R.string.eng_weeks) ?: "w"),
            Measure.MONTHS to (context?.getString(R.string.eng_months) ?: "mon"),
            Measure.YEARS to (context?.getString(R.string.eng_years) ?: "y"),
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
                today == messageTime ->
                    context?.getString(
                        R.string.eng_today,
                        ", ${date.hours}:${
                            date.minutes.toString()
                                .padStart(2, '0')
                        }"
                    )
                        ?: "${Date()}"
                today.first == messageTime.first -> "$monthStr. ${date.day}"
                else -> "$monthStr. ${date.day}, ${date.year}"
            }
        )
    }

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
        val measureStr = unitStringMap!![time.second]
        return "$diffNumber$measureStr"
    }

    /**
     * Creates a real Date from a formatted date and time
     *
     * @param dateStr date in format "yyyy/MM/dd HH:mm:ss"
     * @return the created Date
     */
    @SuppressLint("SimpleDateFormat")
    fun createDate(format: String, dateStr: String): Date? {
        return SimpleDateFormat(format).parse(dateStr)

    }


}