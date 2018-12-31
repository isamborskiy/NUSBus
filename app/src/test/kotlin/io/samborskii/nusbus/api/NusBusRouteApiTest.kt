package io.samborskii.nusbus.api

import io.samborskii.nusbus.api.converter.BusRoutesConverterFactory
import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import io.samborskii.nusbus.model.RouteNode
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.net.HttpURLConnection

class NusBusRouteApiTest : BaseMockWebServerTest() {

    private val a1eBus: String = "a1e"
    private val a1eRoutesResponse: String = """
        <html>
        <head>
            <meta http-equiv=Content-Type content="text/html; charset=windows-1252">
            <style type="text/css">
        #time {
            color: #F00;
        }
        </style>
        </head>
        <body bgcolor="#E6E6FA">
        <h2>Service A1E</h2>
        <h2 id="time"><strong>0730hrs to 1000hrs</strong></h2>
        <p><strong>Lower Kent Ridge Road</strong><br />Kent Ridge MRT Station<br />LT 27<br />Opp. UHC</p>
        <p><strong>Kent Ridge Crescent</strong><br />Central Library</p>
        <p><strong>Business Link</strong><br />BIZ 2</p>
        <p><strong>Prince George's Park</strong> <br />PGP</p>
        <br>
        <br>
        </p>
        </body>
        </html>
    """.trimIndent()
    private val a1eRoute: BusRoute = BusRoute(
        a1eBus.toUpperCase(), Hours("0730", "1000"), listOf(
            RouteNode("Lower Kent Ridge Road", listOf("Kent Ridge MRT Station", "LT 27", "Opp. UHC")),
            RouteNode("Kent Ridge Crescent", listOf("Central Library")),
            RouteNode("Business Link", listOf("BIZ 2")),
            RouteNode("Prince George's Park", listOf("PGP"))
        )
    )

    private val a1RoutesResponse: String = """
        <html>
        <head>
            <meta http-equiv=Content-Type content="text/html; charset=windows-1252">
        </head>
        <body bgcolor="#E6E6FA">
        <h2>Service A1</h2>
        <br /><strong>Prince George's Park</strong><br />PGP<br /><br /><strong>Lower Kent Ridge Road</strong><br />Kent Ridge MRT Station<br />LT 27<br />University Hall<br />Opp. UHC<br /><br /><strong>Kent Ridge Crescent</strong><br />Yusof Ishak House<br />Central Library<br /><br /><strong>Kent Ridge Drive</strong><br />LT 13<br />AS 5<br /><br /><strong>Computing Drive</strong><br />COM2<br /><br /><strong>Business Link</strong><br />BIZ 2<br /><br /><strong>Prince George's Park</strong><br />Opp. TCOMS<br />House 7<br />PGP</p><br>
        <br>
        </body>
        </html>
    """.trimIndent()

    private lateinit var api: NusBusRouteApi

    @Before
    fun setup() {
        api = Retrofit.Builder()
            .baseUrl(resolve("/"))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(BusRoutesConverterFactory.create())
            .build()
            .create(NusBusRouteApi::class.java)
    }

    @Test
    fun `bus routes request`() {
        api.busRoute("a1e")
            .test()
            .assertValue { it == a1eRoute }
    }

    override fun response(request: RecordedRequest): MockResponse = when {
        request.path == "/busroutes/$a1eBus.html" -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "text/html; charset=UTF-8")
            .setBody(a1RoutesResponse)

        else -> notFound()
    }
}
