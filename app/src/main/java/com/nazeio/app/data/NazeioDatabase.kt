package com.nazeio.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AliasEntity::class, RiwayatEntity::class, PengingatEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NazeioDatabase : RoomDatabase() {

    abstract fun aliasDao(): AliasDao
    abstract fun riwayatDao(): RiwayatDao
    abstract fun pengingatDao(): PengingatDao

    companion object {
        @Volatile
        private var INSTANCE: NazeioDatabase? = null

        private val MIGRASI_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `riwayat` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`aksi` TEXT NOT NULL, `hasil` TEXT NOT NULL, `waktu` TEXT NOT NULL)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pengingat` (" +
                        "`kode` INTEGER NOT NULL, `waktu` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`kode`))"
                )
            }
        }

        fun ambil(context: Context): NazeioDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NazeioDatabase::class.java,
                    "nazeio.db"
                )
                    .addMigrations(MIGRASI_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
