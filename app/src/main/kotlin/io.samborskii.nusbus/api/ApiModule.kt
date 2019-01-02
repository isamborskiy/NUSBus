package io.samborskii.nusbus.api

import com.fasterxml.jackson.databind.ObjectMapper
import dagger.Module
import dagger.Provides
import io.samborskii.nusbus.api.impl.NusBusClientImpl
import io.samborskii.nusbus.api.impl.NusBusRouteClientImpl
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
open class ApiModule {

    @Singleton
    @Provides
    open fun client(
        @Named("apiHost") host: String,
        okHttpClient: OkHttpClient,
        objectMapper: ObjectMapper
    ): NusBusClient = NusBusClientImpl(host, okHttpClient, objectMapper)

    @Singleton
    @Provides
    open fun routeClient(
        @Named("routeApiHost") host: String,
        okHttpClient: OkHttpClient
    ): NusBusRouteClient = NusBusRouteClientImpl(host, okHttpClient)
}
