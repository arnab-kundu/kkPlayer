package com.akundu.kkplayer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "SongsTable", indices = [Index(value = ["fileName"], unique = true)])
data class SongEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "artist")
    var artist: String,

    @ColumnInfo(name = "fileName")
    var fileName: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "movie")
    var movie: String,

    @ColumnInfo(name = "isDownloaded")
    var isDownloaded: Boolean = false

)