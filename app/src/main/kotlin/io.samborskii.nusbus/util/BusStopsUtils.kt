package io.samborskii.nusbus.util

import io.samborskii.nusbus.model.BusStop

private val whitespaceRegex: Regex = "\\s+".toRegex()

fun List<BusStop>.find(busStopName: String): BusStop? = firstOrNull { it.deepEquals(busStopName) }

fun BusStop.deepEquals(busStopName: String): Boolean {
    if (caption == busStopName || name == busStopName) return true

    var modifiedBusStopName = busStopName.replace(whitespaceRegex, "")
    if (caption == modifiedBusStopName || name == modifiedBusStopName) return true

    modifiedBusStopName = busStopName.replace(".", "")
    return caption == modifiedBusStopName || name == modifiedBusStopName
}
