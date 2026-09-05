package com.akundu.kkplayer.database

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SongDatabaseTest {
    @Test
    fun `builds a database from the application context`() {
        assertNotNull(SongDatabase.getDatabase(RuntimeEnvironment.getApplication()))
    }

    @Test
    fun `reuses a single database instance`() {
        val context = RuntimeEnvironment.getApplication()

        assertSame(SongDatabase.getDatabase(context), SongDatabase.getDatabase(context))
    }

    @Test
    fun `exposes the song dao`() {
        assertNotNull(SongDatabase.getDatabase(RuntimeEnvironment.getApplication()).songDao())
    }
}
