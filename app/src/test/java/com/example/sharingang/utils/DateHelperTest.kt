package com.example.sharingang.utils

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class DateHelperTest {

    val dateHelper = DateHelper(null!!)
    val format = "yyyy/MM/dd HH:mm:ss"

    @Test
    fun createDateIsCorrect() {
        val now = Date()
        val df: DateFormat = SimpleDateFormat(format)
        val dateString = df.format(now)
        val createdDate = dateHelper.createDate(format, dateString)
        assertEquals(now.toString(), createdDate.toString())
    }

    @Test
    fun getDateDifferenceStringIsCorrectForSeconds() {
        val customDate1 = dateHelper.createDate(format,"2020/02/02 12:00:00")!!
        val customDate2 = dateHelper.createDate(format,"2020/02/02 12:00:01")!!
        val customDate3 = dateHelper.createDate(format,"2020/02/02 12:10:59")!!
        val customDate4 = dateHelper.createDate(format,"2020/02/02 12:11:09")!!
        val difference12 = dateHelper.getDateDifferenceString(customDate1, customDate2)
        val difference34 = dateHelper.getDateDifferenceString(customDate3, customDate4)
        assertEquals("1s", difference12)
        assertEquals("10s", difference34)
    }

    @Test
    fun getDateDifferenceStringIsCorrectForMinutes() {
        val customDate1 = dateHelper.createDate(format,"2020/02/02 12:01:00")!!
        val customDate2 = dateHelper.createDate(format,"2020/02/02 12:05:00")!!
        val customDate3 = dateHelper.createDate(format,"2020/02/02 12:55:00")!!
        val customDate4 = dateHelper.createDate(format,"2020/02/02 13:05:00")!!
        val difference12 = dateHelper.getDateDifferenceString(customDate1, customDate2)
        val difference34 = dateHelper.getDateDifferenceString(customDate3, customDate4)
        assertEquals("4min", difference12)
        assertEquals("10min", difference34)
    }

    @Test
    fun getDateDifferenceStringIsCorrectForHours() {
        val customDate1 = dateHelper.createDate(format,"2020/02/02 12:00:00")!!
        val customDate2 = dateHelper.createDate(format,"2020/02/02 13:00:00")!!
        val customDate3 = dateHelper.createDate(format,"2020/02/02 23:00:00")!!
        val customDate4 = dateHelper.createDate(format,"2020/02/03 04:00:00")!!
        val difference12 = dateHelper.getDateDifferenceString(customDate1, customDate2)
        val difference34 = dateHelper.getDateDifferenceString(customDate3, customDate4)
        assertEquals("1h", difference12)
        assertEquals("5h", difference34)
    }

    @Test
    fun getDateDifferenceStringIsCorrectForWeeks() {
        val customDate1 = dateHelper.createDate(format,"2020/02/02 12:00:00")!!
        val customDate2 = dateHelper.createDate(format,"2020/02/09 12:00:00")!!
        val customDate3 = dateHelper.createDate(format,"2020/03/31 12:00:00")!!
        val customDate4 = dateHelper.createDate(format,"2020/04/07 12:00:00")!!
        val difference12 = dateHelper.getDateDifferenceString(customDate1, customDate2)
        val difference34 = dateHelper.getDateDifferenceString(customDate3, customDate4)
        assertEquals("1w", difference12)
        assertEquals("1w", difference34)
    }

    @Test
    fun getDateDifferenceStringIsCorrectForMonths() {
        val customDate1 = dateHelper.createDate(format,"2020/05/02 12:00:00")!!
        val customDate2 = dateHelper.createDate(format,"2020/06/02 12:00:00")!!
        val customDate3 = dateHelper.createDate(format,"2020/12/01 12:00:00")!!
        val customDate4 = dateHelper.createDate(format,"2021/01/01 12:00:00")!!
        val difference12 = dateHelper.getDateDifferenceString(customDate1, customDate2)
        val difference34 = dateHelper.getDateDifferenceString(customDate3, customDate4)
        assertEquals("1mon", difference12)
        assertEquals("1mon", difference34)
    }

    @Test
    fun getDateDifferenceStringIsCorrectForYears() {
        val customDate1 = dateHelper.createDate(format,"2020/02/02 12:00:00")!!
        val customDate2 = dateHelper.createDate(format,"2021/03/02 12:00:00")!!
        val difference = dateHelper.getDateDifferenceString(customDate1, customDate2)
        assertEquals("1y", difference)
    }
}