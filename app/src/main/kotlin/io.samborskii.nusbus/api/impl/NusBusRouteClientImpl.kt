package io.samborskii.nusbus.api.impl

import io.reactivex.Single
import io.samborskii.nusbus.api.NusBusRouteApi
import io.samborskii.nusbus.api.NusBusRouteClient
import io.samborskii.nusbus.api.converter.BusRoutesConverterFactory
import io.samborskii.nusbus.model.BusRoute
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class NusBusRouteClientImpl(hostUrl: String, okHttpClient: OkHttpClient) : NusBusRouteClient {

    private val api: NusBusRouteApi = Retrofit.Builder()
        .baseUrl(hostUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(BusRoutesConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(NusBusRouteApi::class.java)

    override fun busRoute(busName: String): Single<BusRoute> = api.busRoute(busName.toLowerCase())
}
