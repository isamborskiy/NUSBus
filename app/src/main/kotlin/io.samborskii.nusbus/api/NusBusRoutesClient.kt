package io.samborskii.nusbus.api

import io.reactivex.Single

interface NusBusRoutesClient {
    fun busRoutes(busName: String): Single<String>
}
