package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService

interface NusBusClient {
    fun busStops(): Single<List<BusStop>>

    fun shuttleService(busStopName: String): Single<ShuttleService>
}
