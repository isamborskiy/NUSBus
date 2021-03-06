package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import io.samborskii.nusbus.model.BusStop

@Dao
interface BusStopDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(busStops: List<BusStop>)

    @Query("SELECT * FROM bus_stop")
    fun findAll(): Single<List<BusStop>>
}
