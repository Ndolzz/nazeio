package com.nazeio.app.widget

import android.content.Context
import android.os.Handler
import android.os.Looper

/**
 * Mengganti bingkai animasi widget selama layanan siaga berjalan.
 * Laju: Aktif 8 bingkai per detik, Siaga 2 bingkai per detik.
 * Mode hemat baterai melambatkan keduanya.
 */
class PenggerakWidget(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private var jalan = false
    private var nomor = 0

    private val langkah = object : Runnable {
        override fun run() {
            if (!jalan) return
            nomor++
            PembaruWidget.bingkai(context, nomor)
            handler.postDelayed(this, jeda())
        }
    }

    private fun jeda(): Long {
        val hemat = PrefTampilan.hemat(context)
        return when (StatusBersama.status) {
            StatusBersama.Status.AKTIF -> if (hemat) 250L else 125L
            StatusBersama.Status.SIAGA -> if (hemat) 1000L else 500L
            StatusBersama.Status.MATI -> 1000L
        }
    }

    fun mulai() {
        if (jalan) return
        jalan = true
        handler.post(langkah)
    }

    fun berhenti() {
        jalan = false
        handler.removeCallbacks(langkah)
    }
}
