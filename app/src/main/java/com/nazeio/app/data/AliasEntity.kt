package com.nazeio.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Satu baris alias untuk satu aksi sesuai spesifikasi 02.
 * Nama baku aksi disimpan di kolom aksi, alias terkait di kolom alias.
 */
@Entity(tableName = "alias")
data class AliasEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val aksi: String,
    val alias: String
)
