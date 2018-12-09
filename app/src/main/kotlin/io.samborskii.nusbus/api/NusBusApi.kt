package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusStopsResponse
import retrofit2.http.GET

interface NusBusApi {
    @GET("/eventservice.svc/BusStops")
    fun busStops(): Single<BusStopsResponse>
}
