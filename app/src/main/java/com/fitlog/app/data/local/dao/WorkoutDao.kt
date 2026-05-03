package com.fitlog.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fitlog.app.data.local.WorkoutWithExercises
import com.fitlog.app.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAll(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getById(id: String): WorkoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: WorkoutEntity)

    @Update
    suspend fun update(workout: WorkoutEntity)

    @Delete
    suspend fun delete(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :id")
    suspend fun deleteById(id: String)

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWithExercises(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getByIdWithExercises(id: String): WorkoutWithExercises?

    @Query("SELECT * FROM workouts WHERE isTemplate = 1 ORDER BY name ASC")
    fun getTemplates(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE isTemplate = 1 ORDER BY name ASC")
    fun getTemplatesWithExercises(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE isTemplate = 0 ORDER BY date DESC")
    fun getSessionWorkouts(): Flow<List<WorkoutWithExercises>>
}
