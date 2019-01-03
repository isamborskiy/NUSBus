package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import io.samborskii.nusbus.NusBusApplication
import javax.inject.Singleton

@Module
open class DatabaseModule(application: NusBusApplication) {

    private val appDatabase: AppDatabase =
        Room.databaseBuilder(application, AppDatabase::class.java, "nus-bus-db")
            .build()

    @Singleton
    @Provides
    fun busStopDao(): BusStopDao = appDatabase.getBusStopDao()

    @Singleton
    @Provides
    fun shuttleServiceDao(): ShuttleServiceDao = appDatabase.getShuttleServiceDao()

    @Singleton
    @Provides
    fun busRouteDao(): BusRouteDao = appDatabase.getBusRouteDao()
}
