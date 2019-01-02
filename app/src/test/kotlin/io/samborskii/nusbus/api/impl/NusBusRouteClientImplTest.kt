package io.samborskii.nusbus.api.impl

import io.samborskii.nusbus.api.BaseMockWebServerTest
import io.samborskii.nusbus.api.NusBusRouteClient
import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import io.samborskii.nusbus.model.emptyHours
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import java.net.HttpURLConnection

class NusBusRouteClientImplTest : BaseMockWebServerTest() {

    private val a1eBus: String = "A1E"
    private val a1eRouteResponse: String = """
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
        "A1E", Hours("0730", "1000"), listOf(
            "Kent Ridge MRT Station", "LT 27", "Opp. UHC",
            "Central Library",
            "BIZ 2",
            "PGP"
        )
    )

    private val a1Bus: String = "A1"
    private val a1RouteResponse: String = """
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
    private val a1Route: BusRoute = BusRoute(
        "A1", emptyHours, listOf(
            "PGP",
            "Kent Ridge MRT Station", "LT 27", "University Hall", "Opp. UHC",
            "Yusof Ishak House", "Central Library",
            "LT 13", "AS 5",
            "COM2",
            "BIZ 2",
            "Opp. TCOMS", "House 7", "PGP"
        )
    )

    private val wrongBusName: String = "AAA"
    private val wrongBusNameResponse: String = """
        <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
        <head>
            <meta http-equiv="content-type" content="text/html; charset=utf-8" />
            <meta name="viewport" content="width=device-width" />
            <title>404 - Page not found</title>
            <link rel="stylesheet" href="http://www.nus.edu.sg/annualreport/css/error.css" type="text/css" />
                </head>
        <body>

            <div class="error-masthead">
                <a href="http://www.nus.edu.sg"><img src="http://www.nus.edu.sg/annualreport/images/logo-white.png" /></a>
            </div>

            <div class="error">
                <h1 class="error-code">404</h1>
                <h2 class="error-message">Oops, this page does not exist.</h2>
                <div class="error-buttons">
                    <!--link to your website's homepage-->
                    <a href="http://nus.edu.sg/">Return to the homepage</a>
                </div>
            </div>


        </body>
        </html>
    """.trimIndent()

    private val emptyBusName: String = ""
    private val emptyBusNameResponse: String = """
        <!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
        <html><head>
        <title>403 Forbidden</title>
        </head><body>
        <h1>Forbidden</h1>
        <p>You don't have permission to access //mobileportal/busroutes/.html
        on this server.</p>
        </body></html>
    """.trimIndent()

    private lateinit var client: NusBusRouteClient

    @BeforeEach
    fun setup() {
        client = NusBusRouteClientImpl(resolve("/"), OkHttpClient())
    }

    @Test
    fun `bus routes (regular) request`() {
        client.busRoute(a1Bus)
            .test()
            .assertValue { it == a1Route }
    }

    @Test
    fun `bus routes (express) request`() {
        client.busRoute(a1eBus)
            .test()
            .assertValue { it == a1eRoute }
    }

    @Test
    fun `wrong bus name request`() {
        client.busRoute(wrongBusName)
            .test()
            .assertError { it is HttpException && it.code() == HttpURLConnection.HTTP_NOT_FOUND }
    }

    @Test
    fun `empty bus name request`() {
        client.busRoute(emptyBusName)
            .test()
            .assertError { it is HttpException && it.code() == HttpURLConnection.HTTP_FORBIDDEN }
    }

    override fun response(request: RecordedRequest): MockResponse = when {
        request.path == "/busroutes/${a1Bus.toLowerCase()}.html" -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "text/html; charset=UTF-8")
            .setBody(a1RouteResponse)

        request.path == "/busroutes/${a1eBus.toLowerCase()}.html" -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader("Content-Type", "text/html; charset=UTF-8")
            .setBody(a1eRouteResponse)

        request.path == "/busroutes/.html" -> MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_FORBIDDEN)
            .addHeader("Content-Type", "text/html; charset=iso-8859-1")
            .setBody(emptyBusNameResponse)

        else -> notFound()
            .addHeader("Content-Type", "text/html; charset=UTF-8")
            .setBody(wrongBusNameResponse)
    }
}
