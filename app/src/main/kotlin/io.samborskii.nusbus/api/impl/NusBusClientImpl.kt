package io.samborskii.nusbus.api.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import io.samborskii.nusbus.api.NusBusApi
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.ShuttleService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class NusBusClientImpl(hostUrl: String, okHttpClient: OkHttpClient, mapper: ObjectMapper) : NusBusClient {

    private val api: NusBusApi = Retrofit.Builder()
        .baseUrl(hostUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .client(okHttpClient)
        .build()
        .create(NusBusApi::class.java)

    override fun busStops(): Single<List<BusStop>> = api.busStops()
        .map { it.busStopsResult.busStops.sorted() }

    override fun shuttleService(busStopName: String): Single<ShuttleService> = api.shuttleService(busStopName)
        .map { it.shuttleService }
        .map { ShuttleService(it.caption, it.name, it.shuttles.sorted()) }
}
