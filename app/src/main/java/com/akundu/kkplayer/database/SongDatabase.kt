package com.akundu.kkplayer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.akundu.kkplayer.database.dao.SongDao
import com.akundu.kkplayer.database.entity.SongEntity

@Database(entities = [SongEntity::class], version = 1, exportSchema = false)
abstract class SongDatabase : RoomDatabase() {
    // Declare an abstract function that returns the SongDao interface
    abstract fun songDao(): SongDao

    // Define a companion object to provide a static method to get the database instance
    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: SongDatabase? = null

        fun getDatabase(context: Context): SongDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song_database",
                    )
                        .allowMainThreadQueries()
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
