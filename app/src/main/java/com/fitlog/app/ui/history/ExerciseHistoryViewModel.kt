package com.fitlog.app.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.local.dao.SessionExerciseDao
import com.fitlog.app.data.local.entity.ExerciseHistoryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ExerciseHistoryUiState(
    val exerciseName: String = "",
    val recordWeight: Double? = null,
    val recordDate: String? = null,
    val firstWeight: Double? = null,
    val evolution: Double? = null,
    val sessionCount: Int = 0,
    val chartData: List<Pair<String, Double>> = emptyList(),
    val history: List<ExerciseHistoryEntry> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    private val sessionExerciseDao: SessionExerciseDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseName: String = checkNotNull(savedStateHandle["exerciseName"])

    val uiState: StateFlow<ExerciseHistoryUiState> =
        sessionExerciseDao.getExerciseHistory(exerciseName)
            .map { history ->
                if (history.isEmpty()) {
                    return@map ExerciseHistoryUiState(
                        exerciseName = exerciseName,
                        isLoading = false
                    )
                }
                val sortedAsc = history.sortedBy { it.recordedAt }
                val firstWeight = sortedAsc.firstOrNull()?.weightKg
                val recordEntry = history.maxByOrNull { it.weightKg ?: 0.0 }
                val recordWeight = recordEntry?.weightKg
                val recordDate = recordEntry?.recordedAt?.take(10)?.replace("-", "/")
                val evolution = if (firstWeight != null && recordWeight != null) recordWeight - firstWeight else null

                val chartData = sortedAsc.mapNotNull { entry ->
                    val w = entry.weightKg ?: return@mapNotNull null
                    val label = entry.recordedAt.take(10).let { d ->
                        d.substring(5).replace("-", "/")
                    }
                    Pair(label, w)
                }

                ExerciseHistoryUiState(
                    exerciseName = exerciseName,
                    recordWeight = recordWeight,
                    recordDate = recordDate,
                    firstWeight = firstWeight,
                    evolution = evolution,
                    sessionCount = history.size,
                    chartData = chartData,
                    history = history,
                    isLoading = false
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                ExerciseHistoryUiState(exerciseName = exerciseName)
            )
}
