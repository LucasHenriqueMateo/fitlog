package com.fitlog.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitlog.app.data.local.entity.SessionExerciseEntity

@Dao
interface SessionExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: SessionExerciseEntity)

    @Query("SELECT MAX(weightKg) FROM session_exercises WHERE exerciseName = :name")
    suspend fun getMaxWeightForExercise(name: String): Double?

    @Query("SELECT weightKg FROM session_exercises WHERE exerciseName = :name ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLastWeightForExercise(name: String): Double?
}
