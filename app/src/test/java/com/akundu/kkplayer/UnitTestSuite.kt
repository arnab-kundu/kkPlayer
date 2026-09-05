package com.akundu.kkplayer

import com.akundu.kkplayer.data.SongDataProviderTest
import com.akundu.kkplayer.data.SongTest
import com.akundu.kkplayer.database.SongDaoTest
import com.akundu.kkplayer.database.SongDatabaseTest
import com.akundu.kkplayer.database.entity.SongEntityTest
import com.akundu.kkplayer.di.AppModuleImplTest
import com.akundu.kkplayer.domain.RepositoryImplTest
import com.akundu.kkplayer.download.AndroidDownloaderTest
import com.akundu.kkplayer.download.DownloadCompletedReceiverTest
import com.akundu.kkplayer.download.MimeTypeTest
import com.akundu.kkplayer.feature.main.ui.MainPageUtilsTest
import com.akundu.kkplayer.feature.main.viewModel.MainViewModelTest
import com.akundu.kkplayer.feature.player.viewModel.PlayerViewModelTest
import com.akundu.kkplayer.feature.settings.datastore.DataStoreManagerTest
import com.akundu.kkplayer.feature.settings.viewModel.SettingsViewModelTest
import com.akundu.kkplayer.feature.splash.viewModel.SplashViewModelTest
import com.akundu.kkplayer.media.FileResourceTest
import com.akundu.kkplayer.media.FolderFilesTest
import com.akundu.kkplayer.media.MediaMetaDataRetrieverTest
import com.akundu.kkplayer.media.MediaStoreUtilsTest
import com.akundu.kkplayer.media.MetaDataExtractorTest
import com.akundu.kkplayer.network.ApiRequestTest
import com.akundu.kkplayer.network.ConnectionStateMonitorTest
import com.akundu.kkplayer.network.RetrofitRequestTest
import com.akundu.kkplayer.permission.RuntimePermissionTest
import com.akundu.kkplayer.presentation.ViewModelFactoryHelperTest
import com.akundu.kkplayer.provider.FileAccessPermissionProviderTest
import com.akundu.kkplayer.service.BackgroundSoundServiceTest
import com.akundu.kkplayer.service.ServiceToolsTest
import com.akundu.kkplayer.service.StopServiceReceiverTest
import com.akundu.kkplayer.service.notification.PlayerNotificationBuilderTest
import com.akundu.kkplayer.service.playback.AlbumArtExtractorTest
import com.akundu.kkplayer.service.playback.PlaybackQueueControllerTest
import com.akundu.kkplayer.storage.AppFileManagerTest
import com.akundu.kkplayer.storage.ConstantsTest
import com.akundu.kkplayer.storage.EncryptionManagerTest
import com.akundu.kkplayer.storage.FileLocationCategoryTest
import com.akundu.kkplayer.ui.TestTagsTest
import com.akundu.kkplayer.work.DownloadWorkTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

/**
 * Runs every local unit test in one pass, grouped by the layer under test.
 *
 * `./gradlew unitTestSuite` runs the whole suite; `./gradlew testDebugUnitTest` runs the same
 * classes individually and skips this entry point so nothing is executed twice.
 *
 * A new test class has to be listed here as well, otherwise the suite runs without it.
 */
@RunWith(Suite::class)
@SuiteClasses(
    // Data and persistence
    SongTest::class,
    SongDataProviderTest::class,
    SongEntityTest::class,
    SongDaoTest::class,
    SongDatabaseTest::class,
    RepositoryImplTest::class,
    AppModuleImplTest::class,
    // Storage
    AppFileManagerTest::class,
    EncryptionManagerTest::class,
    FileLocationCategoryTest::class,
    ConstantsTest::class,
    // Media
    FileResourceTest::class,
    FolderFilesTest::class,
    MediaMetaDataRetrieverTest::class,
    MediaStoreUtilsTest::class,
    MetaDataExtractorTest::class,
    // Network and downloads
    ApiRequestTest::class,
    RetrofitRequestTest::class,
    ConnectionStateMonitorTest::class,
    AndroidDownloaderTest::class,
    DownloadCompletedReceiverTest::class,
    DownloadWorkTest::class,
    MimeTypeTest::class,
    // Playback service
    BackgroundSoundServiceTest::class,
    PlaybackQueueControllerTest::class,
    AlbumArtExtractorTest::class,
    PlayerNotificationBuilderTest::class,
    ServiceToolsTest::class,
    StopServiceReceiverTest::class,
    AppsNotificationManagerTest::class,
    // Features
    MainViewModelTest::class,
    MainPageUtilsTest::class,
    PlayerViewModelTest::class,
    SettingsViewModelTest::class,
    DataStoreManagerTest::class,
    SplashViewModelTest::class,
    // Platform helpers
    ViewModelFactoryHelperTest::class,
    RuntimePermissionTest::class,
    FileAccessPermissionProviderTest::class,
    ResourceUtilsTest::class,
    LoggTest::class,
    TestTagsTest::class,
    ExampleUnitTest::class,
)
object UnitTestSuite
