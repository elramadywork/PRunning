package com.example.reactiveapp.di

import android.content.Context
import androidx.room.Room
import com.example.reactiveapp.common.Constants.RUNNING_DATABASE_NAME
import com.example.reactiveapp.db.RunningDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideDataBase(
      @ApplicationContext  conrext:Context
    )=Room.databaseBuilder(
        conrext,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()


    @Provides
    @Singleton
    fun provideDao(db:RunningDatabase)=db.getRunDao()
}