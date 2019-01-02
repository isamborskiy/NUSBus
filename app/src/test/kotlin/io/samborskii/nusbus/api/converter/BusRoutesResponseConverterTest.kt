package io.samborskii.nusbus.api.converter

import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import io.samborskii.nusbus.model.emptyHours
import org.assertj.core.api.Java6Assertions
import org.junit.Test

class BusRoutesResponseConverterTest {

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

    @Test
    fun `convert bus route (regular)`() {
        val busRoute = BusRoutesResponseConverter().convert(a1RouteResponse.byteInputStream())
        Java6Assertions.assertThat(busRoute).isEqualTo(a1Route)
    }

    @Test
    fun `convert bus route (express)`() {
        val busRoute = BusRoutesResponseConverter().convert(a1eRouteResponse.byteInputStream())
        Java6Assertions.assertThat(busRoute).isEqualTo(a1eRoute)
    }

    @Test(expected = BusRouteNotFoundException::class)
    fun `wrong bus route response`() {
        BusRoutesResponseConverter().convert(wrongBusNameResponse.byteInputStream())
    }

    @Test(expected = BusRouteNotFoundException::class)
    fun `empty bus route response`() {
        BusRoutesResponseConverter().convert(emptyBusNameResponse.byteInputStream())
    }
}