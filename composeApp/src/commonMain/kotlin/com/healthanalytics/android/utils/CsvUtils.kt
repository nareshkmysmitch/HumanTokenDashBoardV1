package com.healthanalytics.android.utils

import com.healthanalytics.android.data.api.BloodData
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDateTime

object CsvUtils {
    private fun formatDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return try {
            val instant = Instant.parse(isoString)
            val dt: LocalDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            // Format: 21 May 2025 07:05 AM
            val day = dt.dayOfMonth.toString().padStart(2, '0')
            val month = dt.month.name.lowercase().replaceFirstChar { it.uppercaseChar() }
            val year = dt.year
            val hour = if (dt.hour == 0 || dt.hour == 12) 12 else dt.hour % 12
            val minute = dt.minute.toString().padStart(2, '0')
            val ampm = if (dt.hour < 12) "AM" else "PM"
            "$day $month $year $hour:$minute $ampm"
        } catch (e: Exception) {
            isoString
        }
    }

    fun bloodDataListToCsv(metrics: List<BloodData?>): String {
        val header = listOf(
            "Biomarker name",
            "Status",
            "Value",
            "Unit",
            "Ideal Range",
            "Last Updated"
        )
        println("CSV saved to: 2$metrics")
        val rows = metrics.mapNotNull { metric ->
            metric?.let {
                listOf(
                    it.displayName.orEmpty(),
                    it.displayRating.orEmpty(),
                    it.value?.toString().orEmpty(),
                    it.unit.orEmpty(),
                    it.range.orEmpty(),
                    formatDateTime(it.updatedAt)
                ).joinToString(",") { field ->
                    // Escape quotes and commas
                    '"' + field.replace("\"", "\"\"") + '"'
                }
            }
        }
        val previewRows = (listOf(header.joinToString(",")) + rows).take(5).joinToString("\n")
        println("CSV preview (header + rows):\n$previewRows")
        println("CSV saved to: ${rows.size}")
        return (listOf(header.joinToString(",")) + rows).joinToString("\n")
    }
} 