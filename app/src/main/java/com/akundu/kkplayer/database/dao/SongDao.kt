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
    fun addSong(song: SongEntity)

    @Query("SELECT * FROM SongsTable WHERE 1 = 1")
    fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM SongsTable WHERE 1 = 1")
    fun getAllSongsLiveData(): LiveData<List<SongEntity>>

    @Query("SELECT * FROM SongsTable WHERE id = :id")
    fun findSongById(id: Long): SongEntity

    @Query("SELECT * FROM SongsTable WHERE title LIKE :title ")
    fun searchSongByName(title: String?): List<SongEntity>

    @Query("SELECT * FROM SongsTable WHERE title LIKE '%' || :title || '%'")
    fun searchSongByTitle(title: String?): List<SongEntity>

    @Query("UPDATE SongsTable SET isDownloaded = :isDownloaded WHERE id = :id")
    fun updateSongDownloadInfo(id: Long, isDownloaded: Boolean): Int

    @Query("SELECT COUNT(*) FROM SongsTable WHERE 1 = 1")
    fun getTotalCount(): Int

    @Query("DELETE FROM SongsTable WHERE id = :id")
    fun deleteSong(id: Long)

    @Query("DELETE FROM SongsTable WHERE 1 = 1")
    fun truncateTable(): Int
}
