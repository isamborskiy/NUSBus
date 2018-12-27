package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single
import io.samborskii.nusbus.model.ShuttleService

@Dao
interface ShuttleServiceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(shuttleService: ShuttleService)

    @Query("SELECT * FROM shuttle_service WHERE name = :name")
    fun findByName(name: String): Single<ShuttleService>
}
