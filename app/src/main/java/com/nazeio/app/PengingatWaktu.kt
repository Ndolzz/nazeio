package com.nazeio.app

/**
 * Membaca waktu pengingat dari ucapan. Murni tanpa API Android agar mudah diuji.
 * Mendukung: X menit lagi, X jam lagi, pukul H, dan jam H.
 */
object PengingatWaktu {

    private val SATUAN = mapOf(
        "satu" to 1, "dua" to 2, "tiga" to 3, "empat" to 4, "lima" to 5,
        "enam" to 6, "tujuh" to 7, "delapan" to 8, "sembilan" to 9,
        "sepuluh" to 10, "sebelas" to 11, "dua belas" to 12
    )

    fun angka(teks: String): Int? {
        val t = teks.trim()
        t.toIntOrNull()?.let { return it }
        SATUAN[t]?.let { return it }
        val belas = Regex("^([a-z]+) belas$").find(t)
        if (belas != null) {
            val dasar = SATUAN[belas.groupValues[1]]
            if (dasar != null && dasar in 2..9) return dasar + 10
        }
        val puluh = Regex("^([a-z]+) puluh(?: ([a-z]+))?$").find(t)
        if (puluh != null) {
            val dasar = SATUAN[puluh.groupValues[1]]
            if (dasar != null && dasar in 2..9) {
                val sisaKata = puluh.groupValues[2]
                val sisa = if (sisaKata.isBlank()) 0 else (SATUAN[sisaKata] ?: return null)
                return dasar * 10 + sisa
            }
        }
        return null
    }

    /** Mengambil angka dari satu sampai tiga kata terakhir teks. */
    private fun angkaAkhir(teks: String): Int? {
        val kata = teks.trim().split(Regex(" +")).filter { it.isNotBlank() }
        for (n in minOf(3, kata.size) downTo 1) {
            val coba = kata.takeLast(n).joinToString(" ")
            angka(coba)?.let { return it }
        }
        return null
    }

    /**
     * Waktu pengingat dalam milidetik epoch, atau null bila tidak dikenali.
     */
    fun dariUcapan(ucapan: String, sekarang: Long): Long? {
        val iMenit = ucapan.indexOf(" menit lagi")
        if (iMenit >= 0) {
            val n = angkaAkhir(ucapan.substring(0, iMenit)) ?: return null
            if (n in 1..1440) return sekarang + n * 60_000L
            return null
        }
        val iJam = ucapan.indexOf(" jam lagi")
        if (iJam >= 0) {
            val n = angkaAkhir(ucapan.substring(0, iJam)) ?: return null
            if (n in 1..24) return sekarang + n * 3_600_000L
            return null
        }
        val pukul = Regex("pukul (\\d{1,2})(?::?(\\d{2}))?").find(ucapan)
        if (pukul != null) {
            val h = pukul.groupValues[1].toIntOrNull() ?: return null
            val mnt = pukul.groupValues[2].toIntOrNull() ?: 0
            if (h !in 0..23 || mnt !in 0..59) return null
            return jamTanggal(h, mnt, sekarang)
        }
        val jamAngka = Regex("\\bjam (\\d{1,2})\\b").find(ucapan)
        if (jamAngka != null) {
            val h = jamAngka.groupValues[1].toIntOrNull() ?: return null
            if (h !in 0..23) return null
            return jamTanggal(h, 0, sekarang)
        }
        return null
    }

    /** Jam tertentu hari ini, atau besok bila sudah lewat. */
    private fun jamTanggal(jam: Int, menit: Int, sekarang: Long): Long {
        val kal = java.util.GregorianCalendar()
        kal.timeInMillis = sekarang
        kal.set(java.util.Calendar.HOUR_OF_DAY, jam)
        kal.set(java.util.Calendar.MINUTE, menit)
        kal.set(java.util.Calendar.SECOND, 0)
        kal.set(java.util.Calendar.MILLISECOND, 0)
        if (kal.timeInMillis <= sekarang) kal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        return kal.timeInMillis
    }
}
