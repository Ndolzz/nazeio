package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Test

class NormalisasiTeksTest {

    @Test
    fun buangKataPengantar() {
        assertEquals("youtube", NormalisasiTeks.normalisasi("tolong buka youtube"))
        assertEquals("telegram", NormalisasiTeks.normalisasi("Buka aplikasi Telegram!"))
        assertEquals("kamera", NormalisasiTeks.normalisasi("Nazeio buka kamera"))
    }

    @Test
    fun hanyaKataPengantarMenjadiKosong() {
        assertEquals("", NormalisasiTeks.normalisasi("  BUKA  "))
        assertEquals("", NormalisasiTeks.normalisasi("tolong nazeio"))
    }

    @Test
    fun hurufKecilDanTanpaTandaBaca() {
        assertEquals("wa", NormalisasiTeks.normalisasi("WA."))
        assertEquals("whatsapp web", NormalisasiTeks.normalisasi("WhatsApp-Web"))
    }

    @Test
    fun kataBukanDanBuatDipertahankan() {
        assertEquals("bukan youtube", NormalisasiTeks.normalisasi("bukan youtube"))
        assertEquals("buat pengingat", NormalisasiTeks.normalisasi("buat pengingat"))
    }
}
