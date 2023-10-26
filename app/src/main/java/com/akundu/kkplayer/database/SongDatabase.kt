package com.akundu.kkplayer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity


@Database(entities = [SongEntity::class], version = 1)
abstract class SongDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao
}
