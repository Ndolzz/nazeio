package com.nazeio.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PengingatDao {

    @Query("SELECT * FROM pengingat ORDER BY waktu")
    suspend fun semua(): List<PengingatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun tambah(pengingat: PengingatEntity)

    @Query("DELETE FROM pengingat WHERE kode = :kode")
    suspend fun hapus(kode: Int)

    @Query("DELETE FROM pengingat WHERE waktu <= :sekarang")
    suspend fun hapusLewati(sekarang: Long)
}
