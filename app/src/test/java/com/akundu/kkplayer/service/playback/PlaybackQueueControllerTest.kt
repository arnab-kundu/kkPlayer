package com.akundu.kkplayer.service.playback

import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PlaybackQueueControllerTest {
    private lateinit var songDao: SongDao
    private lateinit var controller: PlaybackQueueController

    private fun song(
        id: Long,
        title: String = "Song $id",
        isDownloaded: Boolean = true,
    ) = SongEntity(
        id = id,
        title = title,
        artist = "KK",
        fileName = "song_$id.mp3",
        url = "https://example.com/song_$id.mp3",
        movie = "Movie $id",
        isDownloaded = isDownloaded,
    )

    @Before
    fun setUp() {
        songDao = mock()
        controller = PlaybackQueueController(songDao)
    }

    @Test
    fun `current returns the song matching the given id`() {
        val expected = song(5)
        whenever(songDao.findSongById(5L)).thenReturn(expected)

        assertSame(expected, controller.current(5))
    }

    @Test
    fun `next queries for the song after the current one`() {
        val expected = song(4)
        whenever(songDao.getNextDownloadedSong(4L)).thenReturn(expected)

        assertSame(expected, controller.next(3))
        verify(songDao).getNextDownloadedSong(4L)
    }

    @Test
    fun `next returns null at the end of the playlist`() {
        whenever(songDao.getNextDownloadedSong(11L)).thenReturn(null)

        assertNull(controller.next(10))
    }

    @Test
    fun `previous queries downwards from the current id`() {
        val expected = song(2)
        whenever(songDao.getPreviousDownloadedSong(3L)).thenReturn(expected)

        assertSame(expected, controller.previous(3))
        verify(songDao).getPreviousDownloadedSong(3L)
    }

    @Test
    fun `previous returns null at the start of the playlist`() {
        whenever(songDao.getPreviousDownloadedSong(1L)).thenReturn(null)

        assertNull(controller.previous(1))
    }

    @Test
    fun `onCompletion with repeat none advances to the next song`() {
        val next = song(4)
        whenever(songDao.getNextDownloadedSong(4L)).thenReturn(next)

        val action = controller.onCompletion(RepeatMode.NONE, 3)

        assertEquals(PlaybackQueueController.CompletionAction.Advance(next), action)
    }

    @Test
    fun `onCompletion with repeat none ends the playlist when no next song exists`() {
        whenever(songDao.getNextDownloadedSong(4L)).thenReturn(null)

        val action = controller.onCompletion(RepeatMode.NONE, 3)

        assertEquals(PlaybackQueueController.CompletionAction.EndOfPlaylist, action)
    }

    @Test
    fun `onCompletion with repeat one loops the current song`() {
        val current = song(3)
        whenever(songDao.findSongById(3L)).thenReturn(current)

        val action = controller.onCompletion(RepeatMode.ONE, 3)

        assertEquals(PlaybackQueueController.CompletionAction.LoopCurrent(current), action)
    }

    @Test
    fun `onCompletion with repeat all advances when a next song exists`() {
        val next = song(4)
        whenever(songDao.getNextDownloadedSong(4L)).thenReturn(next)

        val action = controller.onCompletion(RepeatMode.ALL, 3)

        assertEquals(PlaybackQueueController.CompletionAction.Advance(next), action)
    }

    @Test
    fun `onCompletion with repeat all wraps to the start of the playlist`() {
        whenever(songDao.getNextDownloadedSong(11L)).thenReturn(null)
        val first = song(2)
        whenever(songDao.getNextDownloadedSong(1L)).thenReturn(first)

        val action = controller.onCompletion(RepeatMode.ALL, 10)

        assertEquals(PlaybackQueueController.CompletionAction.Advance(first), action)
    }

    @Test
    fun `onCompletion with repeat all ends playback when nothing is downloaded`() {
        whenever(songDao.getNextDownloadedSong(11L)).thenReturn(null)
        whenever(songDao.getNextDownloadedSong(1L)).thenReturn(null)

        val action = controller.onCompletion(RepeatMode.ALL, 10)

        assertEquals(PlaybackQueueController.CompletionAction.EndOfPlaylist, action)
    }

    @Test
    fun `onCompletion falls back to advancing for an unrecognised repeat mode`() {
        val next = song(4)
        whenever(songDao.getNextDownloadedSong(4L)).thenReturn(next)

        val action = controller.onCompletion("something else", 3)

        assertEquals(PlaybackQueueController.CompletionAction.Advance(next), action)
    }
}
