package io.samborskii.nusbus.util

import io.samborskii.nusbus.model.BusStop

private val whitespaceRegex: Regex = "\\s+".toRegex()

fun List<BusStop>.find(busStopName: String): BusStop? {
    var busStop = firstOrNull { it.caption == busStopName || it.name == busStopName }
    if (busStop != null) return busStop

    var modifiedBusStopName = busStopName.replace(whitespaceRegex, "")
    busStop = firstOrNull { it.caption == modifiedBusStopName || it.name == modifiedBusStopName }
    if (busStop != null) return busStop

    modifiedBusStopName = busStopName.replace(".", "")
    return firstOrNull { it.caption == modifiedBusStopName || it.name == modifiedBusStopName }
}
