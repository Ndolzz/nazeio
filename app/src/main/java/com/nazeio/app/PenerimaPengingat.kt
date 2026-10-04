package com.nazeio.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Menerima sinyal AlarmManager lalu menampilkan notifikasi pengingat.
 */
class PenerimaPengingat : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
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
        nm.notify(intent.getIntExtra(EXTRA_ID, 0), notif)
    }

    companion object {
        const val KANAL = "pengingat"
        const val EXTRA_ID = "id"
    }
}
