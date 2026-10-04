package com.nazeio.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RiwayatDao {

    @Query("SELECT * FROM riwayat ORDER BY id DESC LIMIT 50")
    suspend fun semuaTerbaru(): List<RiwayatEntity>

    @Insert
    suspend fun tambah(riwayat: RiwayatEntity)

    @Query("DELETE FROM riwayat WHERE id NOT IN (SELECT id FROM riwayat ORDER BY id DESC LIMIT 50)")
    suspend fun pangkas()

    @Query("DELETE FROM riwayat")
    suspend fun hapusSemua()
}
