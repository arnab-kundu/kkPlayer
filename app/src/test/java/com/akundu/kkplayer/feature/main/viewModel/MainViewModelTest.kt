package com.akundu.kkplayer.feature.main.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.domain.Repository
import com.akundu.kkplayer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: Repository = mock()

    @Test
    fun `updateSongDownloadStatus forwards the id to the repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = MainViewModel(repository)

            viewModel.updateSongDownloadStatus(9L)
            runCurrent()

            verify(repository).updateSongDownloadStatus(9L)
        }

    @Test
    fun `allSongsLiveData exposes the repository live data`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val songs =
                MutableLiveData(
                    listOf(
                        SongEntity(1L, "Tu hi meri sab hay", "KK", "song.mp3", "https://example.com/song.mp3", "Black"),
                    ),
                )
            whenever(repository.getAllSongsLiveData()).thenReturn(songs)
            val viewModel = MainViewModel(repository)

            viewModel.allSongsLiveData()
            runCurrent()

            assertSame(songs, viewModel.songList)
            assertEquals(1, viewModel.songList.value?.size)
        }

    @Test
    fun `allSongsLiveData reflects later repository emissions`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val songs = MutableLiveData<List<SongEntity>>(emptyList())
            whenever(repository.getAllSongsLiveData()).thenReturn(songs)
            val viewModel = MainViewModel(repository)

            viewModel.allSongsLiveData()
            runCurrent()
            songs.value = listOf(SongEntity(2L, "Alvida", "KK", "alvida.mp3", "", "Life...Metro"))

            assertEquals(
                "Alvida",
                viewModel.songList.value
                    ?.single()
                    ?.title,
            )
        }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun `songList is unavailable until allSongsLiveData is called`() {
        MainViewModel(repository).songList
    }
}
