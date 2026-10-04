package com.nazeio.app

/**
 * Menormalisasi teks ucapan sesuai spesifikasi 01:
 * huruf kecil, tanda baca dibuang, kata pengantar dibuang.
 */
object NormalisasiTeks {

    private val KATA_PENGANTAR = setOf(
        "tolong", "buka", "bukakan", "bukan", "aplikasi", "nazeio", "coba",
        "mohon", "dong", "please", "buat"
    )

    fun normalisasi(teks: String): String {
        val bersih = teks.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .trim()
        return bersih.split(Regex(" +"))
            .filter { it.isNotBlank() && it !in KATA_PENGANTAR }
            .joinToString(" ")
    }
}
