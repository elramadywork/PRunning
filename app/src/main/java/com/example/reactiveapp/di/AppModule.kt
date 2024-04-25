package com.example.reactiveapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.reactiveapp.common.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.reactiveapp.common.Constants.KEY_NAME
import com.example.reactiveapp.common.Constants.KEY_WEIGHT
import com.example.reactiveapp.common.Constants.RUNNING_DATABASE_NAME
import com.example.reactiveapp.common.Constants.SHARED_PREFERENCES_NAME
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

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)


    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)


}