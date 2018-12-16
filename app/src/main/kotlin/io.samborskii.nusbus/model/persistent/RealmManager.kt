package io.samborskii.nusbus.model.persistent

import io.realm.Realm
import io.realm.RealmObject

fun <R : RealmObject> R.insert() =
    Realm.getDefaultInstance().use { realm ->
        realm.executeTransaction {
            it.copyToRealm(this)
        }
    }

fun <R : RealmObject> List<R>.insertList() =
    Realm.getDefaultInstance().use { realm ->
        realm.executeTransaction {
            it.copyToRealm(this)
        }
    }
