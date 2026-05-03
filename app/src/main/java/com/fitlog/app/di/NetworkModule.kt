package com.fitlog.app.di

import com.fitlog.app.data.remote.datasource.AuthRemoteDataSource
import com.fitlog.app.data.remote.datasource.SessionRemoteDataSource
import com.fitlog.app.data.remote.datasource.WorkoutRemoteDataSource
import com.fitlog.app.data.repository.AuthRepository
import com.fitlog.app.data.repository.WorkoutRepository
import com.fitlog.app.data.repository.WorkoutRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    companion object {
        @Provides
        @Singleton
        fun provideWorkoutRemoteDataSource(client: SupabaseClient): WorkoutRemoteDataSource =
            WorkoutRemoteDataSource(client)

        @Provides
        @Singleton
        fun provideAuthRemoteDataSource(client: SupabaseClient): AuthRemoteDataSource =
            AuthRemoteDataSource(client)

        @Provides
        @Singleton
        fun provideAuthRepository(authRemoteDataSource: AuthRemoteDataSource): AuthRepository =
            AuthRepository(authRemoteDataSource)

        @Provides
        @Singleton
        fun provideSessionRemoteDataSource(client: SupabaseClient): SessionRemoteDataSource =
            SessionRemoteDataSource(client)
    }
}
