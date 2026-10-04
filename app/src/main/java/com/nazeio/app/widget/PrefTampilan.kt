package com.nazeio.app.widget

import android.content.Context

/**
 * Membaca pilihan tampilan dari berkas yang sama dengan data.Pengaturan,
 * tanpa membuat EncryptedSharedPreferences di setiap bingkai animasi.
 */
object PrefTampilan {

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences("nazeio_biasa", Context.MODE_PRIVATE)

    fun gaya(context: Context): String =
        prefs(context).getString("gaya_animasi", "gelombang") ?: "gelombang"

    fun hemat(context: Context): Boolean =
        prefs(context).getBoolean("hemat_baterai", false)
}
