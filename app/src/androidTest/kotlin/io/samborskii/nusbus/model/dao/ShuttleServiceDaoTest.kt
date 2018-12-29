package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.EmptyResultSetException
import android.support.test.runner.AndroidJUnit4
import io.samborskii.nusbus.model.Shuttle
import io.samborskii.nusbus.model.ShuttleService
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShuttleServiceDaoTest : BaseDaoTest() {

    private val shuttleService: ShuttleService = ShuttleService(
        random.nextStr(),
        random.nextStr(),
        listOf(
            Shuttle(random.nextStr(), "-", "-", "-", "-"),
            Shuttle(random.nextStr(), "-", "-", "-", "-"),
            Shuttle(random.nextStr(), "-", "-", "-", "-")
        )
    )

    @Test
    fun upsertAndGetShuttleServiceByName() {
        database.getShuttleServiceDao().findByName(shuttleService.name)
            .test()
            .assertError { it is EmptyResultSetException }

        database.getShuttleServiceDao().upsert(shuttleService)

        database.getShuttleServiceDao().findByName(shuttleService.name)
            .test()
            .assertValue {
                it.name == shuttleService.name && it.caption == shuttleService.caption
                        && it.shuttles!!.zip(shuttleService.shuttles!!).all { (s1, s2) -> s1.name == s2.name }
            }

        database.getShuttleServiceDao().upsert(shuttleService)

        database.getShuttleServiceDao().findByName(shuttleService.name)
            .test()
            .assertValue {
                it.name == shuttleService.name && it.caption == shuttleService.caption
                        && it.shuttles!!.zip(shuttleService.shuttles!!).all { (s1, s2) -> s1.name == s2.name }
            }
    }
}
