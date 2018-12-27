package io.samborskii.nusbus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStopsResponse(@JsonProperty("BusStopsResult") val busStopsResult: BusStopsResult)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStopsResult(@JsonProperty("busstops") val busStops: List<BusStop>)

@Entity(tableName = "bus_stop")
@JsonIgnoreProperties(ignoreUnknown = true)
data class BusStop(
    @PrimaryKey @JsonProperty("name") val name: String,
    @JsonProperty("caption") val caption: String,
    @JsonProperty("latitude") val latitude: Double,
    @JsonProperty("longitude") val longitude: Double
) : Comparable<BusStop> {

    override fun compareTo(other: BusStop): Int = name.compareTo(other.name)
}
