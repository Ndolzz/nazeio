package com.nazeio.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AliasDao {

    @Query("SELECT * FROM alias")
    fun semua(): Flow<List<AliasEntity>>

    @Query("SELECT * FROM alias WHERE aksi = :aksi")
    suspend fun berdasarkanAksi(aksi: String): List<AliasEntity>

    @Query("SELECT DISTINCT aksi FROM alias")
    suspend fun semuaAksi(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun tambah(alias: AliasEntity)

    @Query("DELETE FROM alias WHERE id = :id")
    suspend fun hapus(id: Long)

    @Query("DELETE FROM alias WHERE aksi = :aksi")
    suspend fun hapusSemuaAksi(aksi: String)
}
