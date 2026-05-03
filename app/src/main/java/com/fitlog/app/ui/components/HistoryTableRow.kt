package com.fitlog.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fitlog.app.data.local.entity.ExerciseHistoryEntry
import com.fitlog.app.ui.theme.AccentAlt

@Composable
fun HistoryTableRow(
    entry: ExerciseHistoryEntry,
    previousWeight: Double?,
    isAlternate: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isAlternate)
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    else
        Color.Transparent

    val isPr = previousWeight == null ||
        (entry.weightKg != null && previousWeight != null && entry.weightKg > previousWeight)

    val status = when {
        entry.weightKg == null -> "—"
        previousWeight == null -> "—"
        entry.weightKg > previousWeight -> "↑"
        entry.weightKg < previousWeight -> "↓"
        else -> "—"
    }
    val statusColor = when (status) {
        "↑" -> AccentAlt
        "↓" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val dateLabel = entry.recordedAt.take(10).let { d ->
        d.substring(8) + "/" + d.substring(5, 7) + "/" + d.substring(0, 4)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = entry.templateName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = entry.weightKg?.let { "${"%.1f".format(it)} kg" } ?: "—",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = statusColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.5f)
        )
    }
}
