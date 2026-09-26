package com.kampplus.hava.feature.favorites.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kampplus.hava.core.database.HavaDatabase
import com.kampplus.hava.feature.favorites.data.local.dao.FavoriteCityDao
import com.kampplus.hava.feature.favorites.data.local.entity.FavoriteCityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteCityDaoTest {

    private lateinit var database: HavaDatabase
    private lateinit var dao: FavoriteCityDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HavaDatabase::class.java).allowMainThreadQueries().build()
        dao = database.favoriteCityDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeAll_returnsMostRecentlyAddedFirst() = runTest {
        dao.upsert(city(id = 1, addedAt = 1_000))
        dao.upsert(city(id = 2, addedAt = 2_000))

        assertEquals(listOf(2L, 1L), dao.observeAll().first().map { it.id })
    }

    @Test
    fun upsert_replacesExistingRowWithSameId() = runTest {
        dao.upsert(city(id = 1, addedAt = 1_000, name = "Eski"))
        dao.upsert(city(id = 1, addedAt = 2_000, name = "Yeni"))

        val all = dao.observeAll().first()
        assertEquals(1, all.size)
        assertEquals("Yeni", all.single().name)
    }

    @Test
    fun deleteById_removesRow() = runTest {
        dao.upsert(city(id = 1, addedAt = 1_000))
        assertTrue(dao.exists(1))

        dao.deleteById(1)

        assertFalse(dao.exists(1))
        assertTrue(dao.observeAll().first().isEmpty())
    }

    private fun city(id: Long, addedAt: Long, name: String = "Ankara") = FavoriteCityEntity(
        id = id,
        name = name,
        region = "Ankara",
        country = "Türkiye",
        latitude = 39.92,
        longitude = 32.85,
        addedAtEpochMillis = addedAt
    )
}
