package com.fitlog.app.di

import android.content.Context
import androidx.room.Room
import com.fitlog.app.data.local.FitLogDatabase
import com.fitlog.app.data.local.dao.ExerciseDao
import com.fitlog.app.data.local.dao.SessionDao
import com.fitlog.app.data.local.dao.SessionExerciseDao
import com.fitlog.app.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFitLogDatabase(@ApplicationContext context: Context): FitLogDatabase =
        Room.databaseBuilder(
            context,
            FitLogDatabase::class.java,
            "fitlog.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideWorkoutDao(db: FitLogDatabase): WorkoutDao = db.workoutDao()

    @Provides
    @Singleton
    fun provideExerciseDao(db: FitLogDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    @Singleton
    fun provideSessionDao(db: FitLogDatabase): SessionDao = db.sessionDao()

    @Provides
    @Singleton
    fun provideSessionExerciseDao(db: FitLogDatabase): SessionExerciseDao = db.sessionExerciseDao()
}
