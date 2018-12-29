package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleConverters
import io.samborskii.nusbus.model.ShuttleService

@Database(entities = [BusStop::class, ShuttleService::class], version = 1, exportSchema = false)
@TypeConverters(ShuttleConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBusStopDao(): BusStopDao

    abstract fun getShuttleServiceDao(): ShuttleServiceDao
}
