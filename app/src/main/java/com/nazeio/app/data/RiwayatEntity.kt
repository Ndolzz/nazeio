package com.nazeio.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Satu baris riwayat perintah sesuai spesifikasi 10.
 */
@Entity(tableName = "riwayat")
data class RiwayatEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val aksi: String,
    val hasil: String,
    val waktu: String
)
