package com.example.sharingang


import androidx.room.TypeConverter

/**
 * A utility object needed to store List fields in our Room entities.
 */
object ListConverter {

    /**
     * Function to convert String into List of String.
     * @param[stringList] The formatted string containing the list elements.
     * @return The list of Strings contained in the param
     */
    @TypeConverter
    fun toList(stringList: String?): List<String?> {
        if (stringList == null) {
            return ArrayList()
        }
        return stringList.split(',')
    }

    /**
     * Function to convert List of Strings into single String.
     * Uses commas to separate elements.
     * @param[list] List of Strings to convert.
     * @return String containing all elements of the List.
     */
    @TypeConverter
    fun fromList(list: List<String?>): String {
        return list.joinToString(separator = ",")
    }
}