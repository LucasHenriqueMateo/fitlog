package com.fitlog.app.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    fun formatDate(isoDate: String): String {
        return try {
            val date = LocalDate.parse(isoDate)
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            isoDate
        }
    }

    fun today(): String = LocalDate.now().toString()

    fun millisToIsoDate(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .toString()
    }
}
