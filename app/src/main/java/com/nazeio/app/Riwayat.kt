package com.nazeio.app

import androidx.compose.runtime.mutableStateListOf

/**
 * Riwayat aksi terakhir, dipakai bagian TERAKHIR di Beranda
 * dan layar Riwayat sesuai desain.
 */
object Riwayat {
    val entri = mutableStateListOf<Triple<String, String, String>>()

    fun tambah(aksi: String, hasil: String) {
        val waktu = android.text.format.DateFormat.format("HH.mm", java.util.Date()).toString()
        entri.add(0, Triple(aksi, hasil, waktu))
        if (entri.size > 50) entri.removeAt(entri.size - 1)
    }
}
