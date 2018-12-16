package io.samborskii.nusbus.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShuttleServiceResponse(@JsonProperty("ShuttleServiceResult") val shuttleService: ShuttleService)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShuttleService(
    @JsonProperty("caption") val caption: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("shuttles") val shuttles: List<Shuttle>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Shuttle(
    @JsonProperty("name") val name: String,
    @JsonProperty("arrivalTime") val arrivalTime: String,
    @JsonProperty("nextArrivalTime") val nextArrivalTime: String,
    @JsonProperty("passengers") val passengers: String,
    @JsonProperty("nextPassengers") val nextPassengers: String
)
