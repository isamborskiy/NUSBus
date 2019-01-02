package io.samborskii.nusbus.model

data class BusRoute(val name: String, val hours: Hours, val route: List<String>)

val emptyHours: Hours = Hours("", "")

data class Hours(val from: String, val to: String)
