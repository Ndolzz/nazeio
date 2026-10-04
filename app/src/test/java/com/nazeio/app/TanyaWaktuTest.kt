package com.nazeio.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TanyaWaktuTest {

    // Waktu 0 adalah 1 Januari 1970, hari Kamis, di Asia Jakarta.
    @Test
    fun namaHariZonaTetap() {
        assertEquals("Kamis", TanyaWaktu.namaHari(0L))
    }

    @Test
    fun tanggalLengkap() {
        assertEquals("1 Januari 1970", TanyaWaktu.tanggal(0L))
    }

    @Test
    fun hitungHariMenuju() {
        assertEquals(1, TanyaWaktu.hariMenuju("berapa hari lagi sampai 2 januari 1970", 0L))
        assertEquals(7, TanyaWaktu.hariMenuju("sampai 8 januari 1970", 0L))
    }

    @Test
    fun tahunDepanBilaSudahLewat() {
        assertEquals(364, TanyaWaktu.hariMenuju("sampai 31 desember", 0L))
    }

    @Test
    fun tanggalTidakSah() {
        assertNull(TanyaWaktu.hariMenuju("sampai 31 februari", 0L))
        assertNull(TanyaWaktu.hariMenuju("sampai 31 bulan fiktif", 0L))
    }

    @Test
    fun tanpaTanggal() {
        assertNull(TanyaWaktu.hariMenuju("berapa hari lagi", 0L))
    }
}
