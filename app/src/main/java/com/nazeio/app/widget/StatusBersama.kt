package com.nazeio.app.widget

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nazeio.app.LayananSiaga

/**
 * Status tunggal Nazeio yang dipakai layanan, layar aplikasi, dan widget.
 * Disimpan ke SharedPreferences agar widget bisa membacanya setelah proses mati.
 */
object StatusBersama {

    enum class Status { MATI, SIAGA, AKTIF }

    var status by mutableStateOf(Status.MATI)
        private set

    private var dimuat = false

    /** Memuat status tersimpan. Bila layanan ternyata tidak berjalan, status dikembalikan ke MATI. */
    fun muat(context: Context) {
        if (dimuat) return
        dimuat = true
        val app = context.applicationContext
        val tersimpan = baca(app)
        status = if (tersimpan != Status.MATI && !LayananSiaga.sedangJalan(app)) Status.MATI else tersimpan
        if (status != tersimpan) simpan(app, status)
    }

    fun set(context: Context, baru: Status) {
        dimuat = true
        status = baru
        simpan(context.applicationContext, baru)
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences("nazeio_status", Context.MODE_PRIVATE)

    private fun baca(context: Context): Status = try {
        Status.valueOf(prefs(context).getString("status", "MATI") ?: "MATI")
    } catch (e: IllegalArgumentException) {
        Status.MATI
    }

    private fun simpan(context: Context, s: Status) {
        prefs(context).edit().putString("status", s.name).apply()
    }
}
