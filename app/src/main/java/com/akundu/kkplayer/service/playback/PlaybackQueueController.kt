package com.akundu.kkplayer.service.playback

import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity
import com.akundu.kkplayer.feature.settings.datastore.RepeatMode

class PlaybackQueueController(
    private val songDao: SongDao,
) {
    sealed interface CompletionAction {
        data class Advance(
            val song: SongEntity,
        ) : CompletionAction

        data class LoopCurrent(
            val song: SongEntity,
        ) : CompletionAction

        data object EndOfPlaylist : CompletionAction
    }

    fun current(currentSongId: Int): SongEntity = songDao.findSongById(currentSongId.toLong())

    fun next(currentSongId: Int): SongEntity? = songDao.getNextDownloadedSong((currentSongId + 1).toLong())

    fun previous(currentSongId: Int): SongEntity? = songDao.getPreviousDownloadedSong(currentSongId.toLong())

    fun onCompletion(
        repeatMode: String,
        currentSongId: Int,
    ): CompletionAction =
        when (repeatMode) {
            RepeatMode.ONE -> CompletionAction.LoopCurrent(current(currentSongId))
            // Reaching the end under RepeatMode.ALL restarts the playlist from the first downloaded song.
            RepeatMode.ALL -> advanceOrEnd(if (next(currentSongId) == null) 0 else currentSongId)
            else -> advanceOrEnd(currentSongId)
        }

    private fun advanceOrEnd(currentSongId: Int): CompletionAction =
        next(currentSongId)?.let { CompletionAction.Advance(it) } ?: CompletionAction.EndOfPlaylist
}
