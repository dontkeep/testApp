package com.exal.testapp.helper.hilt.modules

import android.content.Context
import androidx.room.Room
import com.exal.testapp.data.local.AppDatabase
import com.exal.testapp.data.local.dao.ListDao
import com.exal.testapp.data.local.dao.RemoteKeysDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideListDao(database: AppDatabase): ListDao {
        return database.listDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}