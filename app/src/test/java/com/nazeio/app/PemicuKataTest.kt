package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PemicuKataTest {

    @Test
    fun pemicuDenganPerintah() {
        val h = PemicuKata.cari("Nazeio buka youtube")
        assertTrue(h.ada)
        assertEquals("buka youtube", h.sisa)
    }

    @Test
    fun salahEjaanUmumTetapTerdeteksi() {
        listOf("nasio", "nazio", "nazeo", "naseo", "nasyo", "halo nazeo").forEach {
            assertTrue(it, PemicuKata.cari(it).ada)
        }
    }

    @Test
    fun terpisahMenjadiDuaKata() {
        assertTrue(PemicuKata.cari("na zeo").ada)
    }

    @Test
    fun kataBiasaTidakMemicu() {
        listOf("nasi goreng", "buka radio", "nazar", "negeri", "nama saya").forEach {
            assertFalse(it, PemicuKata.cari(it).ada)
        }
    }

    @Test
    fun kataPenutup() {
        assertTrue(PemicuKata.akhiri("selesai"))
        assertTrue(PemicuKata.akhiri("terima kasih nazeio"))
        assertFalse(PemicuKata.akhiri("buka youtube"))
    }
}
