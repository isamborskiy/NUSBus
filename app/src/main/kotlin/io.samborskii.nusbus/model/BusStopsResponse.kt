package io.samborskii.nusbus.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStopsResponse(@JsonProperty("BusStopsResult") val busStopsResult: BusStopsResult)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStopsResult(@JsonProperty("busstops") val busStops: List<BusStop>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStop(
    @JsonProperty("name") val name: String,
    @JsonProperty("caption") val caption: String,
    @JsonProperty("latitude") val latitude: Double,
    @JsonProperty("longitude") val longitude: Double
)
