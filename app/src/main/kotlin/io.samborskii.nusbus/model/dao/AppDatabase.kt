package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import io.samborskii.nusbus.model.BusStop

@Database(entities = [BusStop::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBusStopDao(): BusStopDao
}
