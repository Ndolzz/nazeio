package com.nazeio.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings

/**
 * Aksi pengaturan sesuai spesifikasi 02 tahap awal:
 * membuka halaman pengaturan yang sesuai, pengguna mengetuk sendiri tombolnya.
 * Tahap lanjut dengan AccessibilityService menyusul.
 */
object AksiPengaturan {

    fun buka(context: Context, aksi: String): Boolean {
        val niat = when (aksi) {
            "panel_bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            "panel_wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
            "panel_pesawat" -> {
                // Android 11 tidak menyediakan intent langsung mode pesawat,
                // pakai panel jaringan sebagai halaman cadangan.
                Intent(Settings.ACTION_WIRELESS_SETTINGS)
            }
            "panel_hotspot" -> Intent(Settings.ACTION_WIRELESS_SETTINGS)
            "panel_lokasi" -> Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            "screenshot" -> return false
            "buka_whatsapp" -> return bukaPaket(context, "com.whatsapp")
            else -> return false
        }
        niat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return cobaMulai(context, niat)
    }

    private fun bukaPaket(context: Context, paket: String): Boolean {
        val niat = context.packageManager.getLaunchIntentForPackage(paket) ?: return false
        niat.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return cobaMulai(context, niat)
    }

    private fun cobaMulai(context: Context, niat: Intent): Boolean {
        return try {
            context.startActivity(niat)
            true
        } catch (e: Exception) {
            false
        }
    }
}
