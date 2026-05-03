package com.fitlog.app.ui.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitlog.app.R
import com.fitlog.app.ui.components.LoadingOverlay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSessionScreen(
    onFinished: () -> Unit,
    viewModel: ActiveSessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.finished) {
        if (uiState.finished) onFinished()
    }

    if (uiState.isLoading) {
        LoadingOverlay()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.workoutName) },
                actions = {
                    TextButton(
                        onClick = viewModel::finishSession,
                        enabled = !uiState.isFinishing
                    ) {
                        Text(
                            text = stringResource(R.string.btn_finish_session),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(uiState.exercises) { index, exercise ->
                ExerciseSessionRow(
                    exercise = exercise,
                    onWeightChange = { viewModel.updateWeight(index, it) }
                )
                Spacer(Modifier.height(8.dp))
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        if (uiState.isFinishing) {
            LoadingOverlay()
        }
    }
}

@Composable
private fun ExerciseSessionRow(
    exercise: ExerciseSessionState,
    onWeightChange: (String) -> Unit
) {
    val inputWeight = exercise.inputWeight
    val recordWeight = exercise.recordWeight

    val borderColor: Color = when {
        inputWeight == null || recordWeight == null -> Color.Gray
        inputWeight > recordWeight -> Color(0xFF00D4AA)
        inputWeight == recordWeight -> Color(0xFFFFD166)
        else -> Color(0xFFFF6B6B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.sets_reps_format, exercise.sets, exercise.reps),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (exercise.recordWeight != null || exercise.lastWeight != null) {
                Spacer(Modifier.height(8.dp))
                Row {
                    exercise.recordWeight?.let {
                        Text(
                            text = stringResource(R.string.label_record_weight, it),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF00D4AA),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    exercise.lastWeight?.let {
                        Text(
                            text = stringResource(R.string.label_last_weight, it),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputWeight?.toString() ?: "",
                    onValueChange = onWeightChange,
                    label = { Text(stringResource(R.string.label_weight_kg)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor.copy(alpha = 0.6f)
                    )
                )
                if (inputWeight != null && recordWeight != null && inputWeight > recordWeight) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = stringResource(R.string.cd_new_record),
                        tint = Color(0xFF00D4AA),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
