package com.healthanalytics.android.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.healthanalytics.android.data.models.home.Range
import com.healthanalytics.android.presentation.theme.FontFamily

// Global spacing configuration for HorizontalBar
data class HorizontalBarSpacing(
    val barSpacing: Dp = 2.dp,
    val arrowBarSpacing: Dp = 2.dp,
    val barRangeSpacing: Dp = 8.dp,
    val rangeRatingSpacing: Dp = 8.dp,
)

@Composable
fun HorizontalBar(
    ranges: List<Range>,
    value: Double,
    modifier: Modifier = Modifier,
    spacing: HorizontalBarSpacing = HorizontalBarSpacing(),
) {
    val sortedRanges = ranges.sortedBy { it.id }
    val barHeight = 12.dp
    val arrowHeight = 12.dp
    val density = LocalDensity.current

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        // Arrow and Bars
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(barHeight + arrowHeight + spacing.arrowBarSpacing)
        ) {
            // Draw value arrow first (so it appears behind the bars)
            Canvas(
                modifier = Modifier.fillMaxWidth().height(arrowHeight).align(Alignment.TopCenter)
            ) {
                val valuePosition = calculateValuePosition(
                    value, sortedRanges, size.width, density, spacing.barSpacing
                )
                if (valuePosition != null) {
                    drawArrow(
                        position = valuePosition,
                        color = getColorForRating(
                            sortedRanges.firstOrNull { range ->
                                isValueInRange(value, range.range)
                            }?.ratingRank ?: 0
                        ),
                    )
                }
            }

            // Draw bars
            Canvas(
                modifier = Modifier.fillMaxWidth().height(barHeight).align(Alignment.BottomCenter)
            ) {
                val barSpacingPx = with(density) { spacing.barSpacing.toPx() }
                val totalSpacing = barSpacingPx * (sortedRanges.size - 1)
                val availableWidth = size.width.minus(totalSpacing)
                val barWidth = availableWidth / sortedRanges.size

                sortedRanges.forEachIndexed { index, range ->
                    val color = getColorForRating(range.ratingRank ?: 0)
                    val xPosition = index * (barWidth + barSpacingPx)
                    drawRect(
                        color = color,
                        topLeft = Offset(x = xPosition, y = 0f),
                        size = androidx.compose.ui.geometry.Size(barWidth, size.height)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.barRangeSpacing))

        // Range Labels (centered in white space between bars)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val labelCount = sortedRanges.size - 1
            if (labelCount > 0) {
                for (i in 0 until labelCount) {
                    val currentRange = parseRange(sortedRanges[i].range)
                    val label = currentRange.second.toString()
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.pilMedium()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.rangeRatingSpacing))

        // Rating Labels
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            sortedRanges.forEach { range ->
                Text(
                    text = range.displayRating ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    fontFamily = FontFamily.regular()
                )
            }
        }
    }
}

private fun getColorForRating(rating: Int): Color {
    return when (rating) {
        0 -> Color(0xFFD6E7FF)
        1 -> Color(0xFFCC0B00)
        2 -> Color(0xFFF4978A)
        3 -> Color(0xFFF4C764)
        4 -> Color(0xFF8DE28D)
        5 -> Color(0xFF1F7A4C)
        6 -> Color(0xFFEF9D5A)
        else -> Color(0xFFD6E7FF)
    }
}

private fun calculateValuePosition(
    value: Double,
    ranges: List<Range>,
    totalWidth: Float,
    density: androidx.compose.ui.unit.Density,
    barSpacing: Dp,
): Float? {
    val barSpacingPx = with(density) { barSpacing.toPx() }
    val totalSpacing = barSpacingPx * (ranges.size - 1)
    val availableWidth = totalWidth.minus(totalSpacing)
    val barWidth = availableWidth / ranges.size
    val rangeValues = ranges.map { parseRange(it.range) }

    // Find the range that contains the value
    val rangeIndex = rangeValues.indexOfFirst { (min, max) ->
        value >= min && value <= max
    }

    if (rangeIndex == -1) {
        // Value is outside all ranges
        return if (value < rangeValues.first().first) {
            0f
        } else {
            totalWidth
        }
    }

    val (min, max) = rangeValues[rangeIndex]
    val rangeWidth = max - min
    val valuePosition = (value - min) / rangeWidth
    return ((rangeIndex * (barWidth + barSpacingPx)) + (valuePosition * barWidth)).toFloat()
}

private fun parseRange(range: String): Pair<Double, Double> {
    return when {
        range.startsWith("<") -> {
            val value = range.substring(1).trim().toDouble()
            Pair(0.0, value)
        }

        range.startsWith(">") -> {
            val value = range.substring(1).trim().toDouble()
            Pair(value, value + 4) // Adding 4 as per requirement
        }

        range.contains("to") -> {
            val parts = range.split("to").map { it.trim().toDouble() }
            Pair(parts[0], parts[1])
        }

        else -> Pair(0.0, 0.0)
    }
}

private fun isValueInRange(value: Double, range: String): Boolean {
    val (min, max) = parseRange(range)
    return value >= min && value <= max
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawArrow(
    position: Float,
    color: Color,
) {
    val arrowSize = 8.dp.toPx()
    val path = Path().apply {
        moveTo(position, arrowSize)
        lineTo(position - arrowSize, 0f)
        lineTo(position + arrowSize, 0f)
        close()
    }

    drawPath(
        path = path,
        color = color,
    )
}