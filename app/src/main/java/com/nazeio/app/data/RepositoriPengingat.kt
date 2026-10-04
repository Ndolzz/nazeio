package com.nazeio.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.nazeio.app.PenerimaPengingat

/**
 * Repositori pengingat sesuai spesifikasi 07.
 * Alarm dipasang lewat AlarmManager dan dicatat di Room sehingga
 * bisa dipasang ulang setelah HP dinyalakan kembali.
 */
class RepositoriPengingat(private val context: Context) {

    private val dao = NazeioDatabase.ambil(context).pengingatDao()

    suspend fun pasang(waktu: Long): Boolean {
        val kode = (waktu / 1000).toInt()
        if (!aturAlarm(kode, waktu)) return false
        dao.hapusLewati(System.currentTimeMillis())
        dao.tambah(PengingatEntity(kode = kode, waktu = waktu))
        return true
    }

    fun aturAlarm(kode: Int, waktu: Long): Boolean {
        return try {
            val am = context.getSystemService(AlarmManager::class.java) ?: return false
            val niat = Intent(context, PenerimaPengingat::class.java)
                .putExtra(PenerimaPengingat.EXTRA_ID, kode)
            val pi = PendingIntent.getBroadcast(
                context, kode, niat,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, waktu, pi)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun semua(): List<PengingatEntity> {
        dao.hapusLewati(System.currentTimeMillis())
        return dao.semua()
    }

    suspend fun hapus(kode: Int) = dao.hapus(kode)
}
