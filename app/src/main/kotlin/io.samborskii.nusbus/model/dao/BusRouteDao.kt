package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import io.samborskii.nusbus.model.BusRoute

@Dao
interface BusRouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(busRoute: BusRoute)

    @Query("SELECT * FROM bus_route WHERE name = :name")
    fun findByName(name: String): Single<BusRoute>
}
