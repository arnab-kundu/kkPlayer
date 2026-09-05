package com.akundu.kkplayer

import com.akundu.kkplayer.database.SongDatabaseTest
import com.akundu.kkplayer.download.AndroidDownloaderTest
import com.akundu.kkplayer.feature.main.view.MainActivityTest
import com.akundu.kkplayer.feature.player.ui.PlayerPageTest
import com.akundu.kkplayer.feature.player.view.InfoAlertDialogTest
import com.akundu.kkplayer.feature.settings.view.SettingsActivityTest
import com.akundu.kkplayer.feature.settings.view.ui.SettingsScreenTest
import com.akundu.kkplayer.feature.splash.view.SplashPageTest
import com.akundu.kkplayer.storage.AppFileManagerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    SongDatabaseTest::class,
    AppFileManagerTest::class,
    AndroidDownloaderTest::class,
    SettingsScreenTest::class,
    PlayerPageTest::class,
    SplashPageTest::class,
    InfoAlertDialogTest::class,
    SettingsActivityTest::class,
    MainActivityTest::class,
)
object TestSuite
