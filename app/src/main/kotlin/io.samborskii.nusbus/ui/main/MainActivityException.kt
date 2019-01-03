package io.samborskii.nusbus.ui.main

abstract class MainActivityException(message: String) : Exception(message)

class BusStopsLoadingException(message: String) : MainActivityException(message)

class ShuttleLoadingException(message: String) : MainActivityException(message)

class BusRouteLoadingException(message: String) : MainActivityException(message)
