package io.samborskii.nusbus.model.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import java.util.*

open class BaseDaoTest {

    protected val random: Random = Random()

    protected lateinit var database: AppDatabase

    protected fun Random.nextStr(): String = UUID.randomUUID().toString()

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}
