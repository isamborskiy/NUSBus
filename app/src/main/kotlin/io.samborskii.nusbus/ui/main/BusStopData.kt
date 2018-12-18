package io.samborskii.nusbus.ui.main

import io.samborskii.nusbus.model.BusStop
import io.samborskii.nusbus.model.Shuttle

data class BusStopData(
    val busStop: BusStop,
    val shuttles: MutableList<Shuttle> = mutableListOf(),
    var expanded: Boolean = false
)
