package com.nazeio.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nazeio.app.data.RepositoriPengingat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Menerima sinyal AlarmManager lalu menampilkan notifikasi pengingat.
 * Baris pengingat dihapus dari Room setelah ditampilkan.
 */
class PenerimaPengingat : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val kode = intent.getIntExtra(EXTRA_ID, 0)
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(KANAL, "Pengingat Nazeio", NotificationManager.IMPORTANCE_HIGH)
        )
        val buka = android.app.PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val notif = Notification.Builder(context, KANAL)
            .setContentTitle("Pengingat Nazeio")
            .setContentText("Sesuai permintaanmu, waktunya sudah tiba.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(buka)
            .setAutoCancel(true)
            .build()
        nm.notify(kode, notif)

        val tugas = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RepositoriPengingat(context).hapus(kode)
            } finally {
                tugas.finish()
            }
        }
    }

    companion object {
        const val KANAL = "pengingat"
        const val EXTRA_ID = "id"
    }
}
