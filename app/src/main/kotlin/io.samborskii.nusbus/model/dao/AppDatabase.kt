package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.samborskii.nusbus.model.*

@Database(entities = [BusStop::class, ShuttleService::class, BusRoute::class], version = 1, exportSchema = false)
@TypeConverters(ShuttleConverters::class, HoursConverters::class, RouteConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBusStopDao(): BusStopDao

    abstract fun getShuttleServiceDao(): ShuttleServiceDao

    abstract fun getBusRouteDao(): BusRouteDao
}
