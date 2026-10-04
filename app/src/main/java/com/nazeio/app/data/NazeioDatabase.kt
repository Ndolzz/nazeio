package com.nazeio.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AliasEntity::class], version = 1, exportSchema = false)
abstract class NazeioDatabase : RoomDatabase() {

    abstract fun aliasDao(): AliasDao

    companion object {
        @Volatile
        private var INSTANCE: NazeioDatabase? = null

        fun ambil(context: Context): NazeioDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NazeioDatabase::class.java,
                    "nazeio.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
