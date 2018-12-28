package io.samborskii.nusbus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ShuttleServiceResponse(@JsonProperty("ShuttleServiceResult") val shuttleService: ShuttleService)

@Entity(tableName = "shuttle_service")
@JsonIgnoreProperties(ignoreUnknown = true)
data class ShuttleService(
    @JsonProperty("caption") val caption: String,
    @PrimaryKey @JsonProperty("name") val name: String,
    @JsonProperty("shuttles") val shuttles: List<Shuttle>
)

private const val ARRIVING_SHUTTLE_TIME: String = "Arr"
private const val NO_SERVICE_SHUTTLE_TIME: String = "-"

@JsonIgnoreProperties(ignoreUnknown = true)
data class Shuttle(
    @JsonProperty("name") val name: String,
    @JsonProperty("arrivalTime") val arrivalTime: String,
    @JsonProperty("nextArrivalTime") val nextArrivalTime: String,
    @JsonProperty("passengers") val passengers: String,
    @JsonProperty("nextPassengers") val nextPassengers: String
) : Comparable<Shuttle> {

    fun isArriving(): Boolean = arrivalTime == ARRIVING_SHUTTLE_TIME

    fun isNextArriving(): Boolean = nextArrivalTime == ARRIVING_SHUTTLE_TIME

    fun isNoService(): Boolean = arrivalTime == NO_SERVICE_SHUTTLE_TIME

    override fun compareTo(other: Shuttle): Int = name.compareTo(other.name)
}

class ShuttleConverters {

    companion object {

        private const val SEPARATOR: String = ","

        @TypeConverter
        @JvmStatic
        fun fromShuttle(shuttles: List<Shuttle>?): String? = shuttles?.joinToString(SEPARATOR) { it.name }

        @TypeConverter
        @JvmStatic
        fun toShuttle(name: String?): List<Shuttle>? = name
            ?.split(SEPARATOR)
            ?.map { Shuttle(it, "-", "-", "-", "-") }
    }
}
