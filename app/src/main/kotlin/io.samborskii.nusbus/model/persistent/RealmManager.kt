package io.samborskii.nusbus.model.persistent

import io.realm.Realm
import io.realm.RealmObject
import io.realm.kotlin.where

fun <R : RealmObject> R.upsert() =
    Realm.getDefaultInstance().use { realm ->
        realm.executeTransaction {
            it.copyToRealmOrUpdate(this)
        }
    }

fun <R : RealmObject> List<R>.upsertList() =
    Realm.getDefaultInstance().use { realm ->
        realm.executeTransaction {
            it.copyToRealmOrUpdate(this)
        }
    }

inline fun <reified R : RealmObject, E> selectEntities(convert: (R) -> E): List<E> =
    Realm.getDefaultInstance().use { realm ->
        realm.where<R>().findAll().map(convert)
    }
