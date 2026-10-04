package com.nazeio.app

/**
 * Pembungkus pencocok fuzzy agar bisa dipakai untuk nama aplikasi maupun alias.
 */
object PencocokAplikasi {

    fun cariCocok(teks: String, kandidat: List<String>, ambang: Double = 0.75): List<String> {
        return PencocokNama.cariCocok(teks, kandidat, ambang)
    }
}
