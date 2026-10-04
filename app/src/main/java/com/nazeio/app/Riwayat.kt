package com.nazeio.app

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.nazeio.app.data.NazeioDatabase
import com.nazeio.app.data.RiwayatEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Riwayat aksi terakhir, dipakai bagian TERAKHIR di Beranda
 * dan layar Riwayat sesuai desain. Kini tersimpan permanen di Room
 * sesuai spesifikasi 10, dimuat saat aplikasi dibuka.
 */
object Riwayat {
    val entri = mutableStateListOf<Triple<String, String, String>>()

    private var aplikasi: Context? = null
    private var dimuat = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun muat(context: Context) {
        if (aplikasi == null) aplikasi = context.applicationContext
        if (dimuat) return
        dimuat = true
        val db = aplikasi ?: return
        scope.launch {
            val daftar = NazeioDatabase.ambil(db).riwayatDao().semuaTerbaru()
            entri.clear()
            entri.addAll(daftar.map { Triple(it.aksi, it.hasil, it.waktu) })
        }
    }

    fun tambah(aksi: String, hasil: String) {
        val waktu = android.text.format.DateFormat.format("HH.mm", java.util.Date()).toString()
        entri.add(0, Triple(aksi, hasil, waktu))
        if (entri.size > 50) entri.removeAt(entri.size - 1)
        val db = aplikasi ?: return
        scope.launch {
            val dao = NazeioDatabase.ambil(db).riwayatDao()
            dao.tambah(RiwayatEntity(aksi = aksi, hasil = hasil, waktu = waktu))
            dao.pangkas()
        }
    }

    /** Mengosongkan riwayat di memori dan di database. */
    fun hapusSemua() {
        entri.clear()
        val db = aplikasi ?: return
        scope.launch {
            NazeioDatabase.ambil(db).riwayatDao().hapusSemua()
        }
    }
}
