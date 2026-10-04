package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NormalisasiTeksTest {

    @Test
    fun buangKataPengantar() {
        assertEquals("youtube", NormalisasiTeks.normalisasi("tolong buka youtube"))
        assertEquals("telegram", NormalisasiTeks.normalisasi("Buka aplikasi Telegram!"))
        assertEquals("kamera", NormalisasiTeks.normalisasi("Nazeio buka kamera"))
    }

    @Test
    fun hurufKecilDanTanpaTandaBaca() {
        assertEquals("wa", NormalisasiTeks.normalisasi("WA."))
        assertEquals("buka", NormalisasiTeks.normalisasi("  BUKA  "))
    }
}
