package io.samborskii.nusbus

abstract class AppException(message: String) : Exception(message)

class BusStopsLoadingException(message: String) : AppException(message)

class ShuttleLoadingException(message: String) : AppException(message)
