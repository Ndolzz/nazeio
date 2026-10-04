package com.nazeio.app

/**
 * Pencocok nama aplikasi dengan toleransi salah ucap.
 * Memakai jaro winkler sederhana. Ambang awal 0,75 sesuai spesifikasi 01.
 */
object PencocokNama {

    fun jaroWinkler(a: String, b: String): Double {
        val jaro = jaro(a, b)
        val prefiks = a.zip(b).takeWhile { it.first == it.second }.count().coerceAtMost(4)
        return jaro + prefiks * 0.1 * (1 - jaro)
    }

    private fun jaro(a: String, b: String): Double {
        if (a == b) return 1.0
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val jendela = (maxOf(a.length, b.length) / 2) - 1
        val dipakaiA = BooleanArray(a.length)
        val dipakaiB = BooleanArray(b.length)
        var cocok = 0
        for (i in a.indices) {
            val awal = maxOf(0, i - jendela).coerceAtLeast(0)
            val akhir = minOf(i + jendela + 1, b.length)
            for (j in awal until akhir) {
                if (dipakaiB[j]) continue
                if (a[i] != b[j]) continue
                dipakaiA[i] = true
                dipakaiB[j] = true
                cocok++
                break
            }
        }
        if (cocok == 0) return 0.0
        var transposisi = 0
        var k = 0
        for (i in a.indices) {
            if (!dipakaiA[i]) continue
            while (!dipakaiB[k]) k++
            if (a[i] != b[k]) transposisi++
            k++
        }
        transposisi /= 2
        return (cocok / a.length.toDouble() + cocok / b.length.toDouble() +
            (cocok - transposisi) / cocok.toDouble()) / 3.0
    }

    /**
     * Mengembalikan kandidat terbaik beserta skornya.
     * Hasil kosong berarti tidak ada yang cocok.
     */
    fun cariSkor(teks: String, kandidat: List<String>, ambang: Double = 0.75): List<Pair<String, Double>> {
        val skor = kandidat.map { it to jaroWinkler(teks, it.lowercase()) }
            .filter { it.second >= ambang }
            .sortedByDescending { it.second }
        val terbaik = skor.firstOrNull()?.second ?: return emptyList()
        return skor.filter { it.second >= terbaik - 0.02 }
    }

    /**
     * Mengembalikan nama terbaik dari kandidat yang cocok dengan ambang tertentu.
     * Hasil kosong berarti tidak ada yang cocok.
     */
    fun cariCocok(teks: String, kandidat: List<String>, ambang: Double = 0.75): List<String> =
        cariSkor(teks, kandidat, ambang).map { it.first }
}
