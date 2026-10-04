package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PencocokKontakTest {

    private val kontak = listOf(
        Kontak("Budi Santoso", "081200000001"),
        Kontak("Budi Hartono", "081200000002"),
        Kontak("Sinta", "081200000003"),
        Kontak("Rina Melati", "081200000004")
    )

    @Test
    fun namaSamaMunculSemua() {
        val hasil = PencocokKontak.pilih("budi", kontak)
        assertEquals(2, hasil.size)
        assertTrue(hasil.any { it.nama == "Budi Santoso" })
        assertTrue(hasil.any { it.nama == "Budi Hartono" })
    }

    @Test
    fun satuCocokan() {
        val hasil = PencocokKontak.pilih("sinta", kontak)
        assertEquals(listOf("Sinta"), hasil.map { it.nama })
    }

    @Test
    fun tidakAdaYangMirip() {
        val hasil = PencocokKontak.pilih("komputer", kontak)
        assertTrue(hasil.isEmpty())
    }

    @Test
    fun daftarKosong() {
        assertTrue(PencocokKontak.pilih("budi", emptyList()).isEmpty())
    }
}
