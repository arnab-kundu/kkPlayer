package com.akundu.kkplayer.di

import com.akundu.kkplayer.domain.RepositoryImpl
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AppModuleImplTest {
    private lateinit var appModule: AppModule

    @Before
    fun setUp() {
        appModule = AppModuleImpl(RuntimeEnvironment.getApplication())
    }

    @Test
    fun `provides an api client`() {
        assertNotNull(appModule.api)
    }

    @Test
    fun `provides a database`() {
        assertNotNull(appModule.database)
    }

    @Test
    fun `provides a repository backed by the api and database`() {
        assertTrue(appModule.repository is RepositoryImpl)
    }

    @Test
    fun `each dependency is created once`() {
        assertSame(appModule.api, appModule.api)
        assertSame(appModule.database, appModule.database)
        assertSame(appModule.repository, appModule.repository)
    }
}
