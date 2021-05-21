package com.example.sharingang.utils

import java.util.*
import java.util.concurrent.TimeUnit

object DateHelper {
    fun getDateDifferenceInDays(startDate: Date, endDate: Date): Long {
        return TimeUnit.DAYS.convert(endDate.time - startDate.time,
            TimeUnit.MILLISECONDS)
    }

}