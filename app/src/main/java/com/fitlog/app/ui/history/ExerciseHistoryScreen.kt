package com.fitlog.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitlog.app.ui.components.ExerciseEvolutionChart
import com.fitlog.app.ui.components.HistoryTableRow
import com.fitlog.app.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseHistoryScreen(
    onBack: () -> Unit,
    viewModel: ExerciseHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.exerciseName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingOverlay()
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Cards de destaque
            item {
                Spacer(Modifier.height(16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        HighlightCard(
                            emoji = "\uD83C\uDFC6",
                            label = "Recorde",
                            value = uiState.recordWeight?.let { "${"%.1f".format(it)} kg" } ?: "—",
                            sub = uiState.recordDate ?: ""
                        )
                    }
                    item {
                        HighlightCard(
                            emoji = "\uD83D\uDCC8",
                            label = "Evolução",
                            value = uiState.evolution?.let {
                                val sign = if (it >= 0) "+" else ""
                                "$sign${"%.1f".format(it)} kg"
                            } ?: "—",
                            sub = "${uiState.firstWeight?.let { "${"%.1f".format(it)} kg" } ?: "?"} → ${uiState.recordWeight?.let { "${"%.1f".format(it)} kg" } ?: "?"}"
                        )
                    }
                    item {
                        HighlightCard(
                            emoji = "\uD83D\uDD01",
                            label = "Sessões",
                            value = "${uiState.sessionCount}",
                            sub = "registros"
                        )
                    }
                }
            }

            // Gráfico de evolução
            item {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Evolução de carga",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
                ExerciseEvolutionChart(
                    data = uiState.chartData,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Tabela de histórico
            if (uiState.history.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Data", "Treino", "Peso", "").forEach { header ->
                            Text(
                                text = header,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                val historyAsc = uiState.history.sortedBy { it.recordedAt }
                itemsIndexed(uiState.history) { index, entry ->
                    val prevEntry = if (index < uiState.history.size - 1) uiState.history[index + 1] else null
                    HistoryTableRow(
                        entry = entry,
                        previousWeight = prevEntry?.weightKg,
                        isAlternate = index % 2 == 1
                    )
                }
            }
        }
    }
}

@Composable
private fun HighlightCard(
    emoji: String,
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier.width(140.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (sub.isNotEmpty()) {
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
