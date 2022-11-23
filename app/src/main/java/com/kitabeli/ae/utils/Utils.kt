package com.kitabeli.ae.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat

const val DATE_FORMAT_SERVER = "yyyy-mm-dd'T'hh:mm:ssZZZZ"
const val DATE_FORMAT_DD_MMM_YYYY = "dd mmm yyyy"


@SuppressLint("SimpleDateFormat")
fun getAbbreviatedFromDateTime(dateTime: String, dateFormat: String, field: String): String? {
    val input = SimpleDateFormat(dateFormat)
    val output = SimpleDateFormat(field)
    try {
        val getAbbreviate = input.parse(dateTime)    // parse input
        return output.format(getAbbreviate)    // format output
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return null
}