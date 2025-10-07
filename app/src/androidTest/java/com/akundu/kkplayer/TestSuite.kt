package com.akundu.kkplayer

import com.akundu.kkplayer.database.SongDatabaseTest
import com.akundu.kkplayer.download.AndroidDownloaderTest
import com.akundu.kkplayer.storage.AppFileManagerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(SongDatabaseTest::class, AndroidDownloaderTest::class, AppFileManagerTest::class)
object TestSuite {
    private const val DELAY = 300

    @JvmStatic
    fun delay() {
        try {
            Thread.sleep(DELAY.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun delay(timeInMilliSecond: Int) {
        try {
            Thread.sleep(timeInMilliSecond.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
