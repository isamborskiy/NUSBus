package io.samborskii.nusbus.api.converter

import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
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

private const val HEADER_TAG: String = "h2"
private const val HEADER_PREFIX: String = "Service"

private const val HOURS_ELEMENT_ID: String = "time"
private val HOURS_REGEX: Regex = "\\d{4}".toRegex()

private class BusRoutesResponseConverter : Converter<ResponseBody, BusRoute> {

    override fun convert(value: ResponseBody): BusRoute {
        value.use { responseBody ->
            val document = Jsoup.parse(responseBody.byteStream(), Charsets.UTF_8.displayName(), "")
            val body = document.body()

            val headerTag = body.getElementsByTag(HEADER_TAG).first()
            val name = headerTag.text()
                .removePrefix(HEADER_PREFIX)
                .trim()

            val hoursTag = body.getElementById(HOURS_ELEMENT_ID)
            val hours = hoursTag?.let { timeTag ->
                val hoursRaw = timeTag.text()
                val hoursList = HOURS_REGEX.findAll(hoursRaw).map { it.value }
                Hours(hoursList.first(), hoursList.last())
            } ?: Hours("", "")

            var routeTag = (hoursTag ?: headerTag).nextElementSibling()
            val routes = generateSequence {
                routeTag = routeTag.nextElementSibling()
                routeTag
            }.toList()

            return BusRoute("", Hours("", ""), listOf())
        }
    }
}