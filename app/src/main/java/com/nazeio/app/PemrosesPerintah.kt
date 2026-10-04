package com.nazeio.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nazeio.app.data.Pengaturan
import com.nazeio.app.data.RepositoriAlias
import kotlinx.coroutines.flow.first

/**
 * Memproses satu perintah ucapan menjadi aksi nyata.
 * Dipakai oleh layanan siaga dan layar Beranda.
 * Sesuai spesifikasi 01, 02, 03, dan permintaan tambahan:
 * buka aplikasi, kirim chat WhatsApp, cari di YouTube, tanya AI.
 */
class PemrosesPerintah(private val context: Context) {

    private val repositori = RepositoriAlias(context)
    private val pengaturan = Pengaturan(context)
    private val klien = KlienModel(pengaturan)
    private val peluncur = PeluncurAplikasi(context)

    sealed class Hasil {
        data class Selesai(val pesan: String) : Hasil()
        data class Bicara(val pesan: String) : Hasil()
    }

    suspend fun proses(teks Mentah: String): Hasil = prosesMentah(teks Mentah)
    suspend fun prosesMentah(ucapan: String): Hasil {
        val bersih = NormalisasiTeks.normalisasi(ucapan)
        if (bersih.isBlank()) return Hasil.Bicara("Tidak terdengar, coba lagi")

        // Akhiri percakapan.
        if (bersih == "selesai" || bersih == "terima kasih" || bersih == "terima kasih nazeio") {
            return Hasil.Selesai("Sampai jumpa")
        }

        // Cari di YouTube: buka youtube carikan sesuatu.
        if (bersih.contains("youtube") && (bersih.contains("cari") || bersih.contains("carikan"))) {
            val query = bersih
                .replace("buka youtube", "")
                .replace("buka aplikasi youtube", "")
                .replace("youtube", "")
                .replace("carikan", "")
                .replace("cari", "")
                .replace("tolong", "")
                .trim()
            if (query.isBlank()) return Hasil.Bicara("Cari apa di YouTube")
            return if (cariYoutube(query)) Hasil.Selesai("Mencari " + query + " di YouTube")
            else Hasil.Bicara("YouTube tidak terpasang")
        }

        // WhatsApp: kirim chat ke seseorang.
        if (bersih.contains("kirim chat") || bersih.contains("kirim pesan") || bersih.contains("kirim wa")) {
            val tujuan = bersih
                .replace("kirim chat", "")
                .replace("kirim pesan", "")
                .replace("kirim wa", "")
                .replace("ke", "", false)
                .trim()
            return if (bukaAplikasi("WhatsApp")) {
                Hasil.Selesai(
                    if (tujuan.isBlank()) "WhatsApp dibuka. Sebutkan penerima dan isi chat."
                    else "WhatsApp dibuka untuk " + tujuan + ". Isi chat diketik lewat aksesibilitas setelah diizinkan."
                )
            } else {
                Hasil.Bicara("WhatsApp tidak terpasang")
            }
        }

        // Alias dari spesifikasi 02.
        val aksiCocok = repositori.aksiUntukAlias(bersih)
        if (aksiCocok.size == 1) {
            val nama = aksiCocok[0]
            return if (AksiPengaturan.buka(context, nama)) {
                Hasil.Selesai("Membuka " + nama.replace("panel_", "").replace("_", " "))
            } else {
                Hasil.Bicara("Aksi belum bisa dijalankan di perangkat ini")
            }
        }
        if (aksiCocok.size > 1) {
            return Hasil.Bicara("Ada beberapa yang mirip. Mana yang dimaksud")
        }

        // Buka aplikasi.
        val daftar = peluncur.daftarAplikasi()
        val cocok = PencocokNama.cariCocok(bersih, daftar.map { it.label })
        if (cocok.size == 1) {
            val app = daftar.first { it.label == cocok[0] }
            return if (peluncur.buka(app)) Hasil.Selesai("Membuka " + app.label)
            else Hasil.Bicara("Aplikasi tidak ditemukan")
        }
        if (cocok.size > 1) {
            return Hasil.Bicara("Ada beberapa yang mirip: " + cocok.joinToString(", "))
        }

        // Pertanyaan AI: langsung tanya model, kalau tidak ada kunci buka aplikasi AI.
        val pertanyaan = buangPembukaAi(bersih)
        val jawab = klien.tanya(pertanyaan)
        return when (jawab) {
            is KlienModel.Hasil.Sukses -> {
                if (PembicaraEksternal.pilihAplikasiAi(context, daftar.map { it.label }) == null) {
                    Hasil.Selesai("Belum ada kunci API. Buka Claude atau GPT untuk bertanya")
                } else {
                    Hasil.Selesai("Jawaban ada di layar")
                }.let { hasil ->
                    // Jawaban penuh tetap diinginkan dibacakan bila memakai API.
                    if (jawab.jawaban.isNotBlank() && pengaturan.kunciApi.isNotBlank()) {
                        Hasil.Bicara(Pembicara.ringkasUntukBaca(jawab.jawaban))
                    } else hasil
                }
            }
            is KlienModel.Hasil.Gagal -> {
                if (bukaAplikasiAi(context)) {
                    Hasil.Selesai("Membuka aplikasi AI untuk pertanyaan Anda")
                } else {
                    Hasil.Bicara(jawab.pesan)
                }
            }
        }
    }

    private fun buangPembukaAi(teks: String): String {
        var hasil = teks
        listOf(
            "tolong berikan jawaban", "berikan jawaban", "tolong carikan tentang",
            "carikan tentang", "tolong carikan", "carikan", "apa itu", "tolong"
        ).forEach { hasil = hasil.replace(it, "") }
        return hasil.trim()
    }

    private fun cariYoutube(query: String): Boolean {
        return try {
            val url = "https://www.youtube.com/results?search_query=" + Uri.encode(query)
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun bukaAplikasi(nama: String): Boolean {
        val daftar = peluncur.daftarAplikasi()
        val cocok = PencocokNama.cariCocok(NormalisasiTeks.normalisasi(nama), daftar.map { it.label })
        if (cocok.size != 1) return false
        return peluncur.buka(daftar.first { it.label == cocok[0] })
    }

    companion object {
        fun bukaAplikasiAi(context: Context): Boolean {
            val peluncur = PeluncurAplikasi(context)
            val daftar = peluncur.daftarAplikasi()
            val target = daftar.firstOrNull {
                it.label.contains("Claude", true) || it.label.contains("ChatGPT", true) ||
                    it.label.contains("GPT", true)
            }
            return target != null && peluncur.buka(target)
        }
    }
}

private object PembicaraEksternal {
    fun pilihAplikasiAi(context: Context, label: List<String>): String? {
        return label.firstOrNull {
            it.contains("Claude", true) || it.contains("ChatGPT", true) || it.contains("GPT", true)
        }
    }
}
