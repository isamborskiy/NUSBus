package io.samborskii.nusbus.model.dao

import android.support.test.runner.AndroidJUnit4
import io.samborskii.nusbus.model.BusStop
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BusStopDaoTest : BaseDaoTest() {

    private val busStop: BusStop = BusStop(
        random.nextStr(),
        random.nextStr(),
        random.nextDouble(),
        random.nextDouble()
    )

    @Test
    fun upsertAndGetBusStops() {
        database.getBusStopDao().findAll()
            .test()
            .assertValue { it.isEmpty() }

        database.getBusStopDao().upsert(listOf(busStop))

        database.getBusStopDao().findAll()
            .test()
            .assertValue { it.size == 1 && it.first() == busStop }

        database.getBusStopDao().upsert(listOf(busStop))

        database.getBusStopDao().findAll()
            .test()
            .assertValue { it.size == 1 && it.first() == busStop }
    }
}
