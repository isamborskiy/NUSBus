package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.EmptyResultSetException
import android.support.test.runner.AndroidJUnit4
import io.samborskii.nusbus.model.BusRoute
import io.samborskii.nusbus.model.Hours
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusRouteDaoTest : BaseDaoTest() {

    private val busRoute: BusRoute = BusRoute(
        random.nextStr(),
        Hours(random.nextStr(), random.nextStr()),
        (1..10).map { random.nextStr() }
    )

    @Test
    fun upsertAndGetBusRouteByName() {
        database.getBusRouteDao().findByName(busRoute.name)
            .test()
            .assertError { it is EmptyResultSetException }

        database.getBusRouteDao().upsert(busRoute)

        database.getBusRouteDao().findByName(busRoute.name)
            .test()
            .assertValue {
                it.name == busRoute.name && it.hours == busRoute.hours
                        && it.route.zip(busRoute.route).all { (s1, s2) -> s1 == s2 }
            }

        database.getBusRouteDao().upsert(busRoute)

        database.getBusRouteDao().findByName(busRoute.name)
            .test()
            .assertValue {
                it.name == busRoute.name && it.hours == busRoute.hours
                        && it.route.zip(busRoute.route).all { (s1, s2) -> s1 == s2 }
            }
    }
}
