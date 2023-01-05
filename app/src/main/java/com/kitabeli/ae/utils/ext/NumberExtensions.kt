package com.kitabeli.ae.utils.ext

import java.text.SimpleDateFormat
import java.util.*

fun Long.formatAmount(): String {
    return String.format("%,d", this).replace(",", ".")
}

fun Long.toStandardRupiah(): String {
    return "Rp${String.format("%,d", this).replace(",", ".")}"
}

fun Long?.toFormattedDate(format: String = "dd MMM yyyy, HH:mm"): String {
    val dateMilliSeconds = this?.times(1000) ?: 0
    val date = Date(dateMilliSeconds)
    val indoLocale = Locale("id", "ID")
    val sdf = SimpleDateFormat(format, indoLocale)
    return "${sdf.format(date)} ${getLocalizedTimeZone()}"
}

private fun getLocalizedTimeZone(): String {
    val timezone = TimeZone.getDefault().getDisplayName(Locale("id", "ID"))
    var localizedTimeZone = "WIB"
    if (timezone.contains("Tengah")) {
        localizedTimeZone = "WITA"
    } else if (timezone.contains("Timur")) {
        localizedTimeZone = "WIT"
    }
    return localizedTimeZone
}