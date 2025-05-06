package com.example.triplyst.data.notification

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationRepository(dataSource: NotificationDataSource): NotificationRepository {
        return NotificationRepositoryImpl(dataSource)
    }
}
