package com.nazeio.app

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

/**
 * Layanan aksesibilitas untuk menangkap layar sesuai spesifikasi 14.
 * Pengguna menyalakannya satu kali di pengaturan aksesibilitas,
 * setelah itu perintah suara tangkap layar memicu aksi tangkap
 * layar bawaan sistem.
 */
class LayananTangkapan : AccessibilityService() {

    override fun onServiceConnected() {
        layanan = this
    }

    override fun onDestroy() {
        layanan = null
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    companion object {

        @Volatile
        private var layanan: LayananTangkapan? = null

        /** True bila layanan sudah dinyalakan di pengaturan aksesibilitas. */
        fun aktif(context: Context): Boolean {
            val diaktifkan = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            return diaktifkan.contains(
                ComponentName(context, LayananTangkapan::class.java).flattenToString()
            )
        }

        /** Memicu aksi tangkap layar sistem lewat layanan yang aktif. */
        fun tangkap(): Boolean {
            val l = layanan ?: return false
            return try {
                l.performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
            } catch (e: Exception) {
                false
            }
        }

        /** Membuka halaman pengaturan aksesibilitas. */
        fun bukaPengaturan(context: Context) {
            val niat = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(niat)
        }
    }
}
