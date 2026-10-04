package com.nazeio.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Penyimpanan pengaturan dan kunci API sesuai spesifikasi 03.
 * Kunci API disimpan terenkripsi, tidak di SharedPreferences biasa.
 */
class Pengaturan(context: Context) {

    private val aman: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "nazeio_aman",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val biasa: SharedPreferences =
        context.getSharedPreferences("nazeio_biasa", Context.MODE_PRIVATE)

    var penyediaApi: String
        get() = aman.getString("penyedia_api", "gemini") ?: "gemini"
        set(nilai) { aman.edit().putString("penyedia_api", nilai).apply() }

    var kunciApi: String
        get() = aman.getString("kunci_api", "") ?: ""
        set(nilai) { aman.edit().putString("kunci_api", nilai).apply() }

    var batasHarian: Int
        get() = biasa.getInt("batas_harian", 50)
        set(nilai) { biasa.edit().putInt("batas_harian", nilai).apply() }

    var pemakaianHari: Int
        get() = biasa.getInt("pemakaian_" + hariIni(), 0)
        set(nilai) { biasa.edit().putInt("pemakaian_" + hariIni(), nilai).apply() }

    val pemakaianHampirHabis: Boolean
        get() = pemakaianHari >= batasHarian * 4 / 5 && pemakaianHari < batasHarian

    var gayaAnimasi: String
        get() = biasa.getString("gaya_animasi", "gelombang") ?: "gelombang"
        set(nilai) { biasa.edit().putString("gaya_animasi", nilai).apply() }

    var hematBaterai: Boolean
        get() = biasa.getBoolean("hemat_baterai", false)
        set(nilai) { biasa.edit().putBoolean("hemat_baterai", nilai).apply() }

    private fun hariIni(): String =
        android.text.format.DateFormat.format("yyyyMMdd", java.util.Date()).toString()
}
