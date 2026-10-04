package com.nazeio.app

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

/** Satu pintu untuk menyalakan dan mematikan layanan siaga dari layar aplikasi. */
object ControlLayanan {

    fun nyalakan(context: Context) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, LayananSiaga::class.java).setAction(LayananSiaga.AKSI_NYALAKAN)
        )
    }

    fun matikan(context: Context) {
        context.stopService(Intent(context, LayananSiaga::class.java))
    }
}
