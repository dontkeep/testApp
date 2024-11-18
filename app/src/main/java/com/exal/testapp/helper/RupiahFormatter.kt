package com.exal.testapp.helper

import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(number: Int): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    return formatter.format(number)
}