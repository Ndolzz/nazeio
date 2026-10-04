package com.nazeio.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Satu pengingat yang dipasang, sesuai spesifikasi 07.
 * Kode sama dengan requestCode AlarmManager sehingga mudah dihapus.
 */
@Entity(tableName = "pengingat")
data class PengingatEntity(
    @PrimaryKey val kode: Int,
    val waktu: Long
)
