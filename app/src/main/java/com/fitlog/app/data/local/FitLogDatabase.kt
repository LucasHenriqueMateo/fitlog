package com.fitlog.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitlog.app.data.local.dao.ExerciseDao
import com.fitlog.app.data.local.dao.WorkoutDao
import com.fitlog.app.data.local.entity.ExerciseEntity
import com.fitlog.app.data.local.entity.WorkoutEntity

@Database(
    entities = [WorkoutEntity::class, ExerciseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FitLogDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
}
