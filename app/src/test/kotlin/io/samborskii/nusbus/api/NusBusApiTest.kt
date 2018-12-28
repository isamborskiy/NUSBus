package io.samborskii.nusbus.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.samborskii.nusbus.model.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.HttpURLConnection

class NusBusApiTest : BaseMockWebServerTest() {

    private val mapper: ObjectMapper = jacksonObjectMapper()

    private val busStopResponse: BusStopsResponse = BusStopsResponse(
        BusStopsResult(
            listOf(
                BusStop("BIZ2", "BIZ 2", 1.29333997411937, 103.775159716606),
                BusStop("CENLIB", "Central Library", 1.29649996757507, 103.772399902344),
                BusStop("BG-MRT", "Botanic Gardens MRT", 1.32270002365112, 103.815101623535)
            )
        )
    )
    private val shuttleServiceResponse: ShuttleServiceResponse = ShuttleServiceResponse(
        ShuttleService(
            "COM2",
            "COM2",
            listOf(
                Shuttle("A1", "1", "6", "-", "-"),
                Shuttle("D1(To BIZ2)", "28", "38", "-", "-"),
                Shuttle("A2", "14", "24", "-", "-")
            )
        )
    )
    private val unknownBusStopName: String = "UNKNOWN_BUS_STOP_NAME"
    private val unknownShuttleServiceResponse: ShuttleServiceResponse = ShuttleServiceResponse(
        ShuttleService("", "", null)
    )

    private lateinit var api: NusBusApi

    @Before
    fun setup() {
        api = Retrofit.Builder()
            .baseUrl(resolve("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(NusBusApi::class.java)
    }

    @Test
    fun `bus stops request`() {
        api.busStops()
            .test()
            .assertValue { it == busStopResponse }
    }

    @Test
    fun `shuttle service request`() {
        api.shuttleService(shuttleServiceResponse.shuttleService.name)
            .test()
            .assertValue { it == shuttleServiceResponse }
    }

    @Test
    fun `shuttle service request of unknown bus stop`() {
        api.shuttleService(unknownBusStopName)
            .test()
            .assertValue { it == unknownShuttleServiceResponse }
    }

    override fun response(request: RecordedRequest): MockResponse = when {
        request.path == "/eventservice.svc/BusStops" -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(mapper.writeValueAsString(busStopResponse))

        request.path.startsWith("/eventservice.svc/Shuttleservice") &&
                request.path.contains(shuttleServiceResponse.shuttleService.name) -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(mapper.writeValueAsString(shuttleServiceResponse))

        request.path.startsWith("/eventservice.svc/Shuttleservice") -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "application/json; charset=UTF-8")
            .setBody(mapper.writeValueAsString(unknownShuttleServiceResponse))

        else -> notFound()
    }
}
