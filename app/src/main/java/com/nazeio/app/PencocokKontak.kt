package com.nazeio.app

/**
 * Memilih kontak yang paling mirip dengan ucapan, memakai jaro winkler
 * seperti pencocokan nama aplikasi. Skor yang berdekatan dianggap sama
 * baik sehingga semuanya disebut untuk klarifikasi.
 */
object PencocokKontak {

    private const val AMBANG = 0.6
    private const val RENTANG = 0.05

    fun pilih(ucapan: String, kandidat: List<Kontak>): List<Kontak> {
        if (kandidat.isEmpty()) return emptyList()
        val skor = kandidat
            .map { it to PencocokNama.jaroWinkler(ucapan, it.nama.lowercase()) }
            .filter { it.second >= AMBANG }
            .sortedByDescending { it.second }
        val terbaik = skor.firstOrNull()?.second ?: return emptyList()
        return skor.filter { it.second >= terbaik - RENTANG }.map { it.first }
    }
}
