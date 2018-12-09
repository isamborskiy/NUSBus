package io.samborskii.nusbus.api.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Single
import io.samborskii.nusbus.api.NusBusApi
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.model.BusStopsResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

private const val API_URL = "https://nextbus.comfortdelgro.com.sg"

class NusBusClientImpl(okHttpClient: OkHttpClient, mapper: ObjectMapper) : NusBusClient {

    private val api: NusBusApi = Retrofit.Builder()
        .baseUrl(API_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(JacksonConverterFactory.create(mapper))
        .client(okHttpClient)
        .build()
        .create(NusBusApi::class.java)

    override fun busStops(): Single<BusStopsResponse> = api.busStops()
}
