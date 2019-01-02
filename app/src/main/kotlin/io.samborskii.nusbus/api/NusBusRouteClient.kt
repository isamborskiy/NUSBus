package io.samborskii.nusbus.api

import io.reactivex.Single
import io.samborskii.nusbus.model.BusRoute

interface NusBusRouteClient {
    fun busRoute(busName: String): Single<BusRoute>
}
