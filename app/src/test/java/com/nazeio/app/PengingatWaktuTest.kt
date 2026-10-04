package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PengingatWaktuTest {

    @Test
    fun menitKata() {
        assertEquals(300_000L, PengingatWaktu.dariUcapan("ingatkan aku lima menit lagi", 0L))
    }

    @Test
    fun menitAngka() {
        assertEquals(1_200_000L, PengingatWaktu.dariUcapan("ingatkan aku 20 menit lagi", 0L))
    }

    @Test
    fun menitGabungan() {
        assertEquals(1_500_000L, PengingatWaktu.dariUcapan("ingatkan aku dua puluh lima menit lagi", 0L))
    }

    @Test
    fun jamLagi() {
        assertEquals(7_200_000L, PengingatWaktu.dariUcapan("ingatkan aku dua jam lagi", 0L))
    }

    @Test
    fun pukulDiMasaDepan() {
        val t = PengingatWaktu.dariUcapan("ingatkan aku pukul 7", 0L)
        assertNotNull(t)
    }

    @Test
    fun tidakDikenali() {
        assertNull(PengingatWaktu.dariUcapan("ingatkan aku nanti", 0L))
    }
}
