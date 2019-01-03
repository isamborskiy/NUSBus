package io.samborskii.nusbus.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverter

@Entity(tableName = "bus_route")
data class BusRoute(
    @PrimaryKey val name: String,
    val hours: Hours,
    val route: List<String>
)

val emptyHours: Hours = Hours("", "")

data class Hours(val from: String, val to: String)

class HoursConverters {

    companion object {

        private const val SEPARATOR: String = ":"

        @TypeConverter
        @JvmStatic
        fun fromHours(hours: Hours?): String? = hours?.let { "${it.from}$SEPARATOR${it.to}" }

        @TypeConverter
        @JvmStatic
        fun toHours(hoursStr: String?): Hours? = hoursStr
            ?.split(SEPARATOR)
            ?.let { Hours(it.first(), it.last()) }
    }
}

class RouteConverters {

    companion object {

        private const val SEPARATOR: String = ","

        @TypeConverter
        @JvmStatic
        fun fromRoute(route: List<String>?): String? = route?.joinToString(SEPARATOR)

        @TypeConverter
        @JvmStatic
        fun toRoute(routeStr: String?): List<String>? = routeStr
            ?.split(SEPARATOR)
            ?.toList()
    }
}
