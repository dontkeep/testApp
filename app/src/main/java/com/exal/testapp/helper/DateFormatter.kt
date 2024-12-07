package com.exal.testapp.helper

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {

    fun localizeDate(utcDate: String): String {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(utcDate)

            val localFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
            localFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            localFormat.format(date ?: return utcDate)
        } catch (e: Exception) {
            e.printStackTrace()
            utcDate
        }
    }

    fun localizeDay(utcDate: String): String {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(utcDate)

            val localFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            localFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            localFormat.format(date ?: return utcDate)
        } catch (e: Exception) {
            e.printStackTrace()
            utcDate
        }
    }

    fun localizeMonth(utcDate: String): String {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(utcDate)

            val localFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            localFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            localFormat.format(date ?: return utcDate)
        } catch (e: Exception) {
            e.printStackTrace()
            utcDate
        }
    }

    fun localizeYear(utcDate: String): String {
        return try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date: Date? = utcFormat.parse(utcDate)

            val localFormat = SimpleDateFormat("YYYY", Locale.getDefault())
            localFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            localFormat.format(date ?: return utcDate)
        } catch (e: Exception) {
            e.printStackTrace()
            utcDate
        }
    }
}