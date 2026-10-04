package com.nazeio.app.data

import android.content.Context
import androidx.room.Database
import RoomBuilderKt
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
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    NazeioDatabase::t::class,
                    "nazeio.db"
                ).build()
                INSTANCE = db
                return db
            }
        }
    }
}
