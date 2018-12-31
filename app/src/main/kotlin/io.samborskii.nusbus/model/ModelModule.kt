package io.samborskii.nusbus.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class ModelModule {

    @Singleton
    @Provides
    open fun objectMapper(): ObjectMapper = jacksonObjectMapper()
}
