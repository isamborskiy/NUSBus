package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusRoute
import retrofit2.http.GET
import retrofit2.http.Path

interface NusBusRouteApi {
    @GET("/busroutes/{busName}.html")
    fun busRoute(@Path("busName") busName: String): Single<BusRoute>
}
