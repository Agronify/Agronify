package com.agronify.android.model.di

import com.agronify.android.util.AppExecutor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideAppExecutor(): AppExecutor {
        return AppExecutor()
    }

    @Provides
    @Named("diskDispatcher")
    fun provideDiskDispatcher(appExecutor: AppExecutor): CoroutineDispatcher {
        return appExecutor.diskIO.asCoroutineDispatcher()
    }

    @Provides
    @Named("networkDispatcher")
    fun provideNetworkDispatcher(appExecutor: AppExecutor): CoroutineDispatcher {
        return appExecutor.networkIO.asCoroutineDispatcher()
    }
}