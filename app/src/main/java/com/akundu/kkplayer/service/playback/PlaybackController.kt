package com.akundu.kkplayer.service.playback

interface PlaybackController {
    fun playPausePlayer()

    fun nextSong()

    fun previousSong()

    fun seekTo(positionMs: Int)

    fun isCurrentlyPlaying(): Boolean

    fun getCurrentSongId(): Int

    fun getCurrentPositionMs(): Int

    fun getDurationMs(): Int
}
