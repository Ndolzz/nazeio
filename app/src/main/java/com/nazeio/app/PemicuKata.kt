package com.nazeio.app

/**
 * Mengenali kata pemicu "Nazeio" dari hasil pengenal suara.
 * Pengenal jarang menulis kata itu persis, jadi dipakai pencocokan mirip.
 */
object PemicuKata {

    data class Hasil(val ada: Boolean, val sisa: String)

    private const val KATA = "nazeio"
    private const val AMBANG = 0.82
    private val ALIAS = setOf(
        "nezio", "nasyo", "nasio", "naseo", "najio", "nazio", "nazyo",
        "nexio", "nenzio", "neziio", "neziyo", "nesio", "negio", "nozio",
        "nazeo", "naezio"
    )
    private val KATA_AKHIR = setOf("selesai", "terima kasih", "sudah cukup", "cukup")

    private fun pecah(teks: String): List<String> =
        teks.lowercase().replace(Regex("[^a-z0-9 ]"), " ").split(" ").filter { it.isNotBlank() }

    private fun mirip(k: String): Boolean =
        k.length >= 4 && (k in ALIAS || PencocokNama.jaroWinkler(k, KATA) >= AMBANG)

    fun cari(teks: String): Hasil {
        val kata = pecah(teks)
        val i = kata.indexOfFirst { mirip(it) }
        if (i >= 0) {
            val sisa = kata.filterIndexed { j, _ -> j != i }.joinToString(" ")
            return Hasil(true, sisa)
        }
        for (j in 0 until kata.size - 1) {
            if (mirip(kata[j] + kata[j + 1])) {
                val sisa = kata.filterIndexed { n, _ -> n != j && n != j + 1 }.joinToString(" ")
                return Hasil(true, sisa)
            }
        }
        return Hasil(false, teks)
    }

    /** Benar bila ucapan hanya berisi kata penutup percakapan. */
    fun akhiri(teks: String): Boolean {
        val tanpaPemicu = pecah(teks).filterNot { mirip(it) }.joinToString(" ")
        return tanpaPemicu in KATA_AKHIR
    }
}
