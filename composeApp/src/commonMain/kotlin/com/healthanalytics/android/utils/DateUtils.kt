package com.healthanalytics.android.utils

import kotlinx.datetime.*
import kotlinx.datetime.format.DateTimeFormat

object DateUtils {
    private const val SERVER_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    /**
     * Get current day with day start time (00:00:00) and day end time (23:59:59.999)
     * @return Pair of start and end LocalDateTime for current day
     */
    fun getCurrentDayStartAndEnd(): Pair<LocalDateTime, LocalDateTime> {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val dayStart = today.atTime(0, 0, 0, 0)
        val dayEnd = today.atTime(23, 59, 59, 999_000_000)

        return Pair(dayStart, dayEnd)
    }

    /**
     * Get week start time (Monday 00:00:00) and week end time (Sunday 23:59:59.999) based on selected date
     * @param selectedDate The date for which to calculate week boundaries
     * @return Pair of start and end LocalDateTime for the week containing the selected date
     */
    fun getWeekStartAndEnd(selectedDate: LocalDate): Pair<LocalDateTime, LocalDateTime> {
        // Find Monday of the week (ISO week starts on Monday)
        val daysFromMonday = selectedDate.dayOfWeek.ordinal
        val weekStart = selectedDate.minus(daysFromMonday, DateTimeUnit.DAY)
        val weekEnd = weekStart.plus(6, DateTimeUnit.DAY)

        val weekStartTime = weekStart.atTime(0, 0, 0, 0)
        val weekEndTime = weekEnd.atTime(23, 59, 59, 999_000_000)

        return Pair(weekStartTime, weekEndTime)
    }

    /**
     * Get month start time (1st day 00:00:00) and month end time (last day 23:59:59.999) based on selected date
     * @param selectedDate The date for which to calculate month boundaries
     * @return Pair of start and end LocalDateTime for the month containing the selected date
     */
    fun getMonthStartAndEnd(selectedDate: LocalDate): Pair<LocalDateTime, LocalDateTime> {
        val monthStart = LocalDate(selectedDate.year, selectedDate.month, 1)
        val monthEnd = monthStart.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)

        val monthStartTime = monthStart.atTime(0, 0, 0, 0)
        val monthEndTime = monthEnd.atTime(23, 59, 59, 999_000_000)

        return Pair(monthStartTime, monthEndTime)
    }

    /**
     * Get year start time (Jan 1st 00:00:00) and year end time (Dec 31st 23:59:59.999) based on selected date
     * @param selectedDate The date for which to calculate year boundaries
     * @return Pair of start and end LocalDateTime for the year containing the selected date
     */
    fun getYearStartAndEnd(selectedDate: LocalDate): Pair<LocalDateTime, LocalDateTime> {
        val yearStart = LocalDate(selectedDate.year, 1, 1)
        val yearEnd = LocalDate(selectedDate.year, 12, 31)

        val yearStartTime = yearStart.atTime(0, 0, 0, 0)
        val yearEndTime = yearEnd.atTime(23, 59, 59, 999_000_000)

        return Pair(yearStartTime, yearEndTime)
    }

    /**
     * Convert LocalDateTime to ISO format string using SERVER_FORMAT
     * Converts from system timezone to UTC before formatting
     * @param dateTime The LocalDateTime to convert (assumed to be in system timezone)
     * @return ISO formatted string in SERVER_FORMAT (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')
     */
    fun toIsoFormat(dateTime: LocalDateTime): String {
        // Convert LocalDateTime (system timezone) to UTC
        val systemTimeZone = TimeZone.currentSystemDefault()
        val instantInSystemTz = dateTime.toInstant(systemTimeZone)
        val utcDateTime = instantInSystemTz.toLocalDateTime(TimeZone.UTC)

        // Format as ISO string with milliseconds
        val isoString = instantInSystemTz.toString()

        // Ensure the format matches SERVER_FORMAT exactly: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
        return if (isoString.contains('.')) {
            // If already has milliseconds, ensure it's 3 digits
            val parts = isoString.split('.')
            val millisPart = parts[1].replace("Z", "").padEnd(3, '0').take(3)
            "${parts[0]}.${millisPart}Z"
        } else {
            // If no milliseconds, add .000
            isoString.replace("Z", ".000Z")
        }
    }

    /**
     * Convert LocalDate to ISO format string at start of day
     * @param date The LocalDate to convert
     * @return ISO formatted string at start of day
     */
    fun toIsoFormat(date: LocalDate): String {
        val dateTime = date.atTime(0, 0, 0, 0)
        return toIsoFormat(dateTime)
    }

    /**
     * Parse ISO format string to LocalDateTime
     * @param isoString The ISO formatted string to parse
     * @return LocalDateTime parsed from the string
     */
    fun fromIsoFormat(isoString: String): LocalDateTime {
        val instant = Instant.parse(isoString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime
    }

    /**
     * Convert LocalDateTime to ISO format using system timezone conversion
     * Equivalent to Java ZonedDateTime approach
     * @param localDateTime The LocalDateTime to convert (assumed to be in system timezone)
     * @return ISO formatted string in SERVER_FORMAT
     */
    fun getIso(localDateTime: LocalDateTime): String {
        // Convert LocalDateTime from system timezone to UTC (equivalent to ZonedDateTime.of + withZoneSameInstant)
        val systemTimeZone = TimeZone.currentSystemDefault()
        val instantInSystemTz = localDateTime.toInstant(systemTimeZone)
        val utcDateTime = instantInSystemTz.toLocalDateTime(TimeZone.UTC)

        // Format using SERVER_FORMAT pattern
        val year = utcDateTime.year.toString().padStart(4, '0')
        val month = utcDateTime.monthNumber.toString().padStart(2, '0')
        val day = utcDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = utcDateTime.hour.toString().padStart(2, '0')
        val minute = utcDateTime.minute.toString().padStart(2, '0')
        val second = utcDateTime.second.toString().padStart(2, '0')
        val millis = (utcDateTime.nanosecond / 1_000_000).toString().padStart(3, '0')

        return "${year}-${month}-${day}T${hour}:${minute}:${second}.${millis}Z"
    }

    /**
     * Convert LocalDate to ISO format using system timezone conversion
     * Uses start of day (00:00:00.000) for the time component
     * @param localDate The LocalDate to convert (assumed to be in system timezone)
     * @return ISO formatted string in SERVER_FORMAT
     */
    fun getIso(localDate: LocalDate): String {
        // Convert LocalDate to LocalDateTime at start of day
        val localDateTime = localDate.atTime(0, 0, 0, 0)


        // Convert LocalDateTime from system timezone to UTC
        val systemTimeZone = TimeZone.currentSystemDefault()
        val instantInSystemTz = localDateTime.toInstant(systemTimeZone)
        val utcDateTime = instantInSystemTz.toLocalDateTime(TimeZone.UTC)

        // Format using SERVER_FORMAT pattern
        val year = utcDateTime.year.toString().padStart(4, '0')
        val month = utcDateTime.monthNumber.toString().padStart(2, '0')
        val day = utcDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = utcDateTime.hour.toString().padStart(2, '0')
        val minute = utcDateTime.minute.toString().padStart(2, '0')
        val second = utcDateTime.second.toString().padStart(2, '0')
        val millis = (utcDateTime.nanosecond / 1_000_000).toString().padStart(3, '0')

        return "${year}-${month}-${day}T${hour}:${minute}:${second}.${millis}Z"
    }

    /**
     * Get current UTC time as LocalDateTime
     * @return Current UTC time as LocalDateTime
     */
    fun getCurrentUtcTime(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return getIso(now)
    }

    /**
     * Format LocalDateTime for display purposes
     * @param dateTime The LocalDateTime to format
     * @param pattern The format pattern (default: "yyyy-MM-dd HH:mm")
     * @return Formatted date string
     */
    fun formatForDisplay(dateTime: LocalDateTime?, pattern: String? = null): String {

        val hour24 = dateTime?.hour
        val amPm = when (hour24) {
            0 -> "AM"
            in 1..11 -> "AM"
            12 -> "PM"
            else -> "PM"
        }

        return when (pattern) {
            "yyyy-MM-dd HH:mm" -> "${dateTime?.date} ${
                dateTime?.hour.toString().padStart(2, '0')
            }:${dateTime?.minute.toString().padStart(2, '0')} $amPm"

            "yyyy-MM-dd" -> dateTime?.date.toString()
            "HH:mm" -> "${dateTime?.hour.toString().padStart(2, '0')}:${
                dateTime?.minute.toString().padStart(2, '0')
            } $amPm"

            else -> {
                "${dateTime?.hour.toString().padStart(2, '0')}:${
                    dateTime?.minute.toString().padStart(2, '0')
                } $amPm"
            }
        }
    }
}
