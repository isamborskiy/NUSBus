package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusStopsResponse

interface NusBusClient {
    fun busStops(): Single<BusStopsResponse>
}
