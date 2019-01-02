package io.samborskii.nusbus.integration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.samborskii.nusbus.api.NusBusClient
import io.samborskii.nusbus.api.NusBusRouteClient
import io.samborskii.nusbus.api.impl.NusBusClientImpl
import io.samborskii.nusbus.api.impl.NusBusRouteClientImpl
import io.samborskii.nusbus.model.emptyHours
import io.samborskii.nusbus.net.API_HOST
import io.samborskii.nusbus.net.ROUTE_API_HOST
import io.samborskii.nusbus.util.find
import io.samborskii.nusbus.util.removeSpecification
import okhttp3.OkHttpClient
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import retrofit2.HttpException

@Tag("integration")
class NusBusAPITest {

    private lateinit var client: NusBusClient
    private lateinit var routeClient: NusBusRouteClient

    @BeforeEach
    fun setup() {
        val okHttpClient = OkHttpClient()
        client = NusBusClientImpl(API_HOST, okHttpClient, jacksonObjectMapper())
        routeClient = NusBusRouteClientImpl(ROUTE_API_HOST, okHttpClient)
    }

    @Test
    fun `request bus stops`() {
        client.busStops()
            .test()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `request existing shuttle service`() {
        client.shuttleService("BIZ2")
            .test()
            .assertValue { it.caption.isNotBlank() && it.name.isNotBlank() && it.shuttles!!.isNotEmpty() }
    }

    @Test
    fun `request non-existing shuttle service`() {
        client.shuttleService("BIIIZ2")
            .test()
            .assertValue { it.caption.isBlank() && it.name.isBlank() && it.shuttles == null }
    }

    @Test
    fun `request existing express bus route`() {
        routeClient.busRoute("A1E")
            .test()
            .assertValue { it.name.isNotBlank() && it.hours != emptyHours && it.route.isNotEmpty() }
    }

    @Test
    fun `request existing regular bus route`() {
        routeClient.busRoute("A1")
            .test()
            .assertValue { it.name.isNotBlank() && it.hours == emptyHours && it.route.isNotEmpty() }
    }

    @Test
    fun `request non-existing bus route`() {
        routeClient.busRoute("AAA")
            .test()
            .assertError { it is HttpException }
    }

    @Test
    fun `request empty bus route`() {
        routeClient.busRoute("")
            .test()
            .assertError { it is HttpException }
    }

    @Test
    fun `request all buses routes`() {
        val busStops = client.busStops().blockingGet()

        val buses = busStops
            .map { it.name }
            .map { client.shuttleService(it).blockingGet() }
            .flatMap { it.shuttles!! }
            .map { it.name }
            .map { it.removeSpecification() }
            .distinct()
            .sorted()

        val busesRoutes = buses
            .map { routeClient.busRoute(it).blockingGet() }

        assertThat(
            busesRoutes
                .all { route ->
                    route.route.all { busStops.find(it) != null }
                },
            equalTo(true)
        )
    }
}
