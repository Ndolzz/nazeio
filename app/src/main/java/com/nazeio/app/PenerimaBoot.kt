package com.nazeio.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nazeio.app.data.RepositoriPengingat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Memasang ulang semua pengingat yang tersimpan di Room
 * setelah HP dinyalakan kembali, sesuai spesifikasi 07.
 */
class PenerimaBoot : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val tugas = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repo = RepositoriPengingat(context)
                repo.semua().forEach { repo.aturAlarm(it.kode, it.waktu) }
            } finally {
                tugas.finish()
            }
        }
    }
}
