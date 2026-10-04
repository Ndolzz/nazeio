package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PencocokNamaTest {

    private val kandidat = listOf(
        "YouTube", "Spotify", "Telegram", "WhatsApp", "Kamera",
        "Chrome", "Maps", "Galeri", "Pengaturan", "Kontak",
        "Kalender", "Jam", "Kalkulator", "Play Store", "Gmail",
        "Drive", "Facebook", "Instagram", "TikTok", "X"
    )

    @Test
    fun ucapanTepat() {
        val hasil = PencocokNama.cariCocok("youtube", kandidat)
        assertEquals(listOf("YouTube"), hasil)
    }

    @Test
    fun ucapanSalahUcapSportify() {
        val hasil = PencocokNama.cariCocok("sportify", kandidat)
        assertTrue(hasil.contains("Spotify"))
    }

    @Test
    fun ucapanTidakCocok() {
        val hasil = PencocokNama.cariCocok("aplikasitakada", kandidat)
        assertTrue(hasil.isEmpty())
    }
}
