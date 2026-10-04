package com.nazeio.app.data

import android.content.Context
import kotlinx.coroutines.flow.first

/**
 * Repositori alias sesuai spesifikasi 02.
 * Alias awal dibawa aplikasi dan diisi ke Room saat pertama kali dipakai.
 */
class RepositoriAlias(private val context: Context) {

    private val dao = NazeioDatabase.ambil(context).aliasDao()

    suspend fun pastikanDataAwal() {
        if (dao.semuaAksi().isNotEmpty()) return
        AWAL.forEach { (aksi, daftar) ->
            daftar.forEach { alias -> dao.tambah(AliasEntity(aksi = aksi, alias = alias)) }
        }
    }

    suspend fun semua(): List<AliasEntity> = dao.semua().first()

    suspend fun tambahAlias(aksi: String, alias: String) {
        dao.tambah(AliasEntity(aksi = aksi, alias = alias.lowercase().trim()))
    }

    suspend fun hapus(id: Long) = dao.hapus(id)

    suspend fun aksiUntukAlias(teks: String): List<String> {
        val semuaAlias = semua()
        val cocok = PencocokNama.cariCocok(teks, semuaAlias.map { it.alias })
        return semuaAlias.filter { it.alias in cocok }.map { it.aksi }.distinct()
    }

    companion object {
        val AWAL: Map<String, List<String>> = mapOf(
            "panel_bluetooth" to listOf("bluetooth", "bt", "blutut"),
            "panel_wifi" to listOf("wifi", "internet nirkabel"),
            "panel_pesawat" to listOf("pesawat", "mode pesawat"),
            "panel_hotspot" to listOf("hotspot", "tethering"),
            "panel_lokasi" to listOf("lokasi", "gps"),
            "screenshot" to listOf("screenshot", "ss", "tangkap layar"),
            "buka_whatsapp" to listOf("whatsapp", "wa")
        )
    }
}
