package io.samborskii.nusbus.model.persistent

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class RealmBusStop(
    @PrimaryKey
    open var name: String = "",
    open var caption: String = "",
    open var latitude: Double = 0.0,
    open var longitude: Double = 0.0
) : RealmObject()
