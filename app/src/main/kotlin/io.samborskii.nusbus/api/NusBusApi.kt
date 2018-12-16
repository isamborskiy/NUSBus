package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusStopsResponse
import io.samborskii.nusbus.model.ShuttleServiceResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NusBusApi {
    @GET("/eventservice.svc/BusStops")
    fun busStops(): Single<BusStopsResponse>

    @GET("/eventservice.svc/Shuttleservice")
    fun shuttleService(@Query("busstopname") busStopName: String): Single<ShuttleServiceResponse>
}
