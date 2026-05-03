package com.fitlog.app.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitlog.app.data.local.dao.SessionDao
import com.fitlog.app.data.local.dao.SessionExerciseDao
import com.fitlog.app.data.local.entity.WorkoutSessionEntity
import com.fitlog.app.domain.model.SessionExercise
import com.fitlog.app.domain.model.WorkoutSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HistorySummary(
    val totalSessions: Int,
    val currentStreak: Int,
    val lastSessionDate: String?,
    val prsThisMonth: Int
)

data class ExerciseSummary(
    val name: String,
    val lastWeight: Double,
    val recordWeight: Double,
    val sessionCount: Int,
    val lastSessionDate: String
)

data class HistoryUiState(
    val summary: HistorySummary = HistorySummary(0, 0, null, 0),
    val lastSession: WorkoutSession? = null,
    val exercises: List<ExerciseSummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val sessionDao: SessionDao,
    private val sessionExerciseDao: SessionExerciseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sessionDao.getAllSessions(),
                sessionExerciseDao.getExerciseSummaries(),
                sessionExerciseDao.getPrsThisMonth()
            ) { sessions, exerciseSummaries, prsThisMonth ->
                Triple(sessions, exerciseSummaries, prsThisMonth)
            }.collect { (sessions, exerciseSummaries, prsThisMonth) ->
                val lastSessionEntity = sessions.firstOrNull()
                val lastSession = if (lastSessionEntity != null) {
                    val exEntities = sessionExerciseDao.getExercisesForSession(lastSessionEntity.id)
                    val exercisesWithPr = exEntities.map { ex ->
                        val maxWeight = sessionExerciseDao.getMaxWeightForExercise(ex.exerciseName)
                        SessionExercise(
                            exerciseName = ex.exerciseName,
                            sets = ex.sets,
                            reps = ex.reps,
                            weightKg = ex.weightKg,
                            isPr = ex.weightKg != null && ex.weightKg == maxWeight
                        )
                    }
                    WorkoutSession(
                        id = lastSessionEntity.id,
                        templateName = lastSessionEntity.templateName,
                        templateCode = lastSessionEntity.templateCode,
                        startedAt = lastSessionEntity.startedAt,
                        exercises = exercisesWithPr
                    )
                } else null

                _uiState.value = HistoryUiState(
                    summary = HistorySummary(
                        totalSessions = sessions.size,
                        currentStreak = computeStreak(sessions),
                        lastSessionDate = lastSessionEntity?.startedAt?.let { formatRelativeDate(it) },
                        prsThisMonth = prsThisMonth
                    ),
                    lastSession = lastSession,
                    exercises = exerciseSummaries.map { entity ->
                        ExerciseSummary(
                            name = entity.exerciseName,
                            lastWeight = entity.lastWeight,
                            recordWeight = entity.recordWeight,
                            sessionCount = entity.sessionCount,
                            lastSessionDate = entity.lastSessionDate
                        )
                    },
                    isLoading = false
                )
            }
        }
    }

    private fun computeStreak(sessions: List<WorkoutSessionEntity>): Int {
        val dates = sessions.mapNotNull {
            runCatching { LocalDate.parse(it.startedAt.substring(0, 10)) }.getOrNull()
        }.toSortedSet()

        if (dates.isEmpty()) return 0

        val mostRecent = dates.last()
        val yesterday = LocalDate.now().minusDays(1)
        if (mostRecent.isBefore(yesterday)) return 0

        var streak = 0
        var cur = mostRecent
        while (dates.contains(cur)) {
            streak++
            cur = cur.minusDays(1)
        }
        return streak
    }

    private fun formatRelativeDate(dateStr: String): String {
        return try {
            val date = LocalDate.parse(dateStr.substring(0, 10))
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(date, today).toInt()
            when {
                days == 0 -> "hoje"
                days == 1 -> "há 1 dia"
                else -> "há $days dias"
            }
        } catch (e: Exception) {
            dateStr.take(10)
        }
    }
}
