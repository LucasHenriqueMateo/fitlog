package com.fitlog.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitlog.app.data.local.entity.ExerciseHistoryEntry
import com.fitlog.app.data.local.entity.ExerciseSummaryEntity
import com.fitlog.app.data.local.entity.SessionExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: SessionExerciseEntity)

    @Query("SELECT MAX(weightKg) FROM session_exercises WHERE exerciseName = :name")
    suspend fun getMaxWeightForExercise(name: String): Double?

    @Query("SELECT weightKg FROM session_exercises WHERE exerciseName = :name ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLastWeightForExercise(name: String): Double?

    @Query("SELECT * FROM session_exercises WHERE sessionId = :sessionId")
    suspend fun getExercisesForSession(sessionId: String): List<SessionExerciseEntity>

    @Query("""
        SELECT exerciseName,
               MAX(weightKg) as recordWeight,
               (SELECT weightKg FROM session_exercises se2
                WHERE se2.exerciseName = se.exerciseName
                ORDER BY recordedAt DESC LIMIT 1) as lastWeight,
               COUNT(*) as sessionCount,
               MAX(recordedAt) as lastSessionDate
        FROM session_exercises se
        WHERE weightKg IS NOT NULL
        GROUP BY exerciseName
        ORDER BY lastSessionDate DESC
    """)
    fun getExerciseSummaries(): Flow<List<ExerciseSummaryEntity>>

    @Query("""
        SELECT se.id, se.sessionId, se.exerciseName, se.sets, se.reps, se.weightKg, se.recordedAt,
               ws.templateName, ws.startedAt as sessionDate
        FROM session_exercises se
        JOIN workout_sessions ws ON ws.id = se.sessionId
        WHERE se.exerciseName = :name AND se.weightKg IS NOT NULL
        ORDER BY se.recordedAt DESC
    """)
    fun getExerciseHistory(name: String): Flow<List<ExerciseHistoryEntry>>

    @Query("""
        SELECT COUNT(DISTINCT exerciseName) FROM session_exercises
        WHERE weightKg = (
            SELECT MAX(weightKg) FROM session_exercises se2
            WHERE se2.exerciseName = session_exercises.exerciseName
        )
        AND strftime('%Y-%m', recordedAt) = strftime('%Y-%m', 'now')
    """)
    fun getPrsThisMonth(): Flow<Int>
}
