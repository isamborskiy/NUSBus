package io.samborskii.nusbus.model.persistent

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.samborskii.nusbus.model.BusStop

@RealmClass
open class RealmBusStop(
    @PrimaryKey
    open var name: String = "",
    open var caption: String = "",
    open var latitude: Double = 0.0,
    open var longitude: Double = 0.0
) : RealmObject()

fun RealmBusStop.fromRealm(): BusStop = BusStop(name, caption, latitude, longitude)

fun BusStop.toRealm(): RealmBusStop = RealmBusStop(name, caption, latitude, longitude)
