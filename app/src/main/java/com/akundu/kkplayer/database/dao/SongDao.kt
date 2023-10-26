package com.akundu.kkplayer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.akundu.kkplayer.database.entity.SongEntity


@Dao
interface SongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSong(song: SongEntity): Boolean

    @Query("SELECT * FROM SongsTable")
    fun getAllSongs(): LiveData<List<SongEntity>>

    @Query("SELECT * FROM SongsTable WHERE id = :id")
    suspend fun findSongById(id: Long): SongEntity

    @Query("SELECT * FROM SongsTable WHERE title LIKE :title ")
    suspend fun searchSongByName(title: String?): LiveData<List<SongEntity>>

    @Query("SELECT * FROM SongsTable WHERE title LIKE '%' || :title || '%'")
    suspend fun searchSongByTitle(title: String?): LiveData<List<SongEntity>>

    @Query("UPDATE SongsTable SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateSongDownloadInfo(id: Long, isDownloaded: Boolean): Boolean

}