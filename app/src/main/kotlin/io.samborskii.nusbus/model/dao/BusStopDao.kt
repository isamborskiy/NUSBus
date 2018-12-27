package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.*
import io.samborskii.nusbus.model.BusStop

private const val BAD_RAW_ID: Long = -1

@Dao
interface BusStopDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(busStop: BusStop): Long

    @Update
    fun update(busStop: BusStop): Int

    @Transaction
    fun upsert(busStop: BusStop): Int = if (insert(busStop) == BAD_RAW_ID) upsert(busStop) else 1

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(busStops: List<BusStop>): List<Long>

    @Update
    fun update(busStops: List<BusStop>): Int

    @Transaction
    fun upsert(busStops: List<BusStop>): Int {
        val ids = insert(busStops)
        val existedBusStops = ids.mapIndexedNotNull { index, rawId ->
            if (rawId == BAD_RAW_ID) busStops[index] else null
        }

        if (existedBusStops.isNotEmpty()) return ids.size + update(existedBusStops)
        return busStops.size
    }

    @Query("SELECT * FROM bus_stop WHERE name = :name")
    fun findByName(name: String): BusStop

    @Query("SELECT * FROM bus_stop")
    fun findAll(): List<BusStop>
}
