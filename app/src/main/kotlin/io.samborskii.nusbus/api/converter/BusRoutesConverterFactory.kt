package io.samborskii.nusbus.api.converter

import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import io.samborskii.nusbus.model.emptyHours
import io.samborskii.nusbus.util.removeSpecification
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.InputStream
import java.lang.reflect.Type

class BusRoutesConverterFactory private constructor() : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> = BusRoutesResponseConverter()

    companion object {
        @JvmStatic
        fun create(): BusRoutesConverterFactory = BusRoutesConverterFactory()
    }
}

private const val PAGE_TITLE_TAG: String = "title"

private const val PAGE_TITLE_403: String = "403 Forbidden"
private const val PAGE_TITLE_404: String = "404 - Page not found"

private const val HEADER_TAG: String = "h2"
private const val HEADER_PREFIX: String = "Service"

private const val HOURS_ELEMENT_ID: String = "time"
private val HOURS_REGEX: Regex = "\\d{4}".toRegex()

private val unknownBusStopsMapping: Map<String, String> = mapOf(
    "Botanic Gardens MRT Station" to "Botanic Gardens MRT",
    "CP 11, Biz 2" to "BIZ 2",
    "House 15" to "PGP Hse 15",
    "House 7" to "PGP Hse 7",
    "IT" to "Information Technology",
    "Kent Ridge MRT Station" to "Kent Ridge MRT",
    "Oei Tiong Ham" to "Oei Tiong Ham Building",
    "Opp. Kent Ridge MRT Station" to "Opp Kent Ridge MRT",
    "Opp. UHC" to "Opp University Health Centre",
    "Opp. University Hall" to "Opp UHall",
    "PGP Residences" to "PGPR",
    "UHC" to "University Health Centre",
    "University Hall" to "UHall",
    "Ventus" to "Ventus (Opp LT13)",
    "Yusof Ishak House" to "YIH"
)

internal class BusRoutesResponseConverter : Converter<ResponseBody, BusRoute> {

    override fun convert(value: ResponseBody): BusRoute = value.use { convert(it.byteStream()) }

    internal fun convert(inputStream: InputStream): BusRoute {
        val document = Jsoup.parse(inputStream, Charsets.UTF_8.displayName(), "")
        val title = document.getElementsByTag(PAGE_TITLE_TAG)
        if (title != null && (title.text() == PAGE_TITLE_403 || title.text() == PAGE_TITLE_404))
            throw BusRouteNotFoundException()

        val body = document.body()

        val headerTag = body.getElementsByTag(HEADER_TAG).first()
        val name = headerTag.text()
            .removePrefix(HEADER_PREFIX)
            .removeSpecification()
            .trim()

        val hoursTag = body.getElementById(HOURS_ELEMENT_ID)
        val hours = hoursTag?.let { timeTag ->
            val hoursRaw = timeTag.text()
            val hoursList = HOURS_REGEX.findAll(hoursRaw).map { it.value }
            Hours(hoursList.first(), hoursList.last())
        } ?: emptyHours

        var route = extractRegularShuttleBusRoute(body)
        if (route.isEmpty()) route = extractExpressShuttleBusRoute(hoursTag ?: headerTag)

        return BusRoute(name, hours, route)
    }

    private fun extractRegularShuttleBusRoute(body: Element): List<String> = body.textNodes().extractRoutes()

    private fun extractExpressShuttleBusRoute(hoursTag: Element): List<String> {
        var routeTag: Element? = hoursTag
        return generateSequence {
            routeTag = routeTag?.nextElementSibling()
            routeTag
        }.toList()
            .flatMap { it.textNodes() }
            .extractRoutes()
    }

    private fun List<TextNode>.extractRoutes(): List<String> = map { it.text() }
        .map { it.removeSpecification() }
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { if (it in unknownBusStopsMapping) unknownBusStopsMapping[it]!! else it }
}

class BusRouteNotFoundException : Exception()
