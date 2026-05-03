package com.fitlog.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitlog.app.data.local.dao.ExerciseDao
import com.fitlog.app.data.local.dao.SessionDao
import com.fitlog.app.data.local.dao.SessionExerciseDao
import com.fitlog.app.data.local.dao.WorkoutDao
import com.fitlog.app.data.local.entity.ExerciseEntity
import com.fitlog.app.data.local.entity.SessionExerciseEntity
import com.fitlog.app.data.local.entity.WorkoutEntity
import com.fitlog.app.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntity::class,
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FitLogDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun sessionDao(): SessionDao
    abstract fun sessionExerciseDao(): SessionExerciseDao
}
