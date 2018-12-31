package io.samborskii.nusbus.model

data class BusRoute(val name: String, val hours: Hours, val route: List<RouteNode>)

data class Hours(val from: String, val to: String)

data class RouteNode(val areaName: String, val links: List<String>)
