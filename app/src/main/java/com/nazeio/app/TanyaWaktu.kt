package com.nazeio.app

import java.util.Calendar
import java.util.GregorianCalendar
import java.util.TimeZone

/**
 * Pertanyaan tentang hari dan tanggal sesuai spesifikasi 09.
 * Murni tanpa API Android agar mudah diuji.
 * Memakai zona waktu Asia Jakarta secara tetap supaya hasilnya pasti.
 */
object TanyaWaktu {

    private val ZONA = TimeZone.getTimeZone("Asia/Jakarta")

    private val HARI = listOf(
        "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
    )

    private val BULAN = listOf(
        "januari", "februari", "maret", "april", "mei", "juni",
        "juli", "agustus", "september", "oktober", "november", "desember"
    )

    private fun kalender(waktu: Long): GregorianCalendar =
        GregorianCalendar(ZONA).apply { timeInMillis = waktu }

    fun namaHari(sekarang: Long): String {
        val kal = kalender(sekarang)
        // Calendar Senin bernilai 2 sampai Minggu bernilai 1.
        return HARI[(kal.get(Calendar.DAY_OF_WEEK) + 5) % 7]
    }

    fun tanggal(sekarang: Long): String {
        val kal = kalender(sekarang)
        val namaBulan = BULAN[kal.get(Calendar.MONTH)]
        val bulanTulis = namaBulan.substring(0, 1).uppercase() + namaBulan.substring(1)
        return kal.get(Calendar.DAY_OF_MONTH).toString() + " " +
            bulanTulis + " " + kal.get(Calendar.YEAR).toString()
    }

    /**
     * Jumlah hari dari sekarang sampai tanggal yang disebut ucapan,
     * atau null bila tanggalnya tidak dikenali.
     * Tanpa tahun, tanggal yang sudah lewat dianggap tahun depan.
     */
    fun hariMenuju(ucapan: String, sekarang: Long): Int? {
        val cocok = Regex("sampai (\\d{1,2}) ([a-z]+)(?: (\\d{4}))?").find(ucapan) ?: return null
        val hari = cocok.groupValues[1].toIntOrNull() ?: return null
        val bulan = BULAN.indexOf(cocok.groupValues[2])
        if (bulan < 0 || hari !in 1..31) return null
        val tahunUcapan = cocok.groupValues[3].toIntOrNull()

        val kalAwal = kalender(sekarang).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun target(tahun: Int): GregorianCalendar = GregorianCalendar(ZONA).apply {
            set(Calendar.YEAR, tahun)
            set(Calendar.MONTH, bulan)
            set(Calendar.DAY_OF_MONTH, hari)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun selisih(t: GregorianCalendar): Int =
            ((t.timeInMillis - kalAwal.timeInMillis) / 86_400_000L).toInt()

        var kalTarget = target(tahunUcapan ?: kalAwal.get(Calendar.YEAR))
        // Kalender longgar menggeser tanggal tidak sah, misalnya 31 februari.
        if (kalTarget.get(Calendar.MONTH) != bulan || kalTarget.get(Calendar.DAY_OF_MONTH) != hari) {
            return null
        }
        if (selisih(kalTarget) < 0 && tahunUcapan == null) {
            kalTarget = target(kalAwal.get(Calendar.YEAR) + 1)
        }
        return selisih(kalTarget)
    }
}
