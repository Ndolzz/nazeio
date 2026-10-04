package com.nazeio.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nazeio.app.data.Pengaturan
import com.nazeio.app.data.RepositoriAlias

/**
 * Memproses satu perintah ucapan menjadi aksi nyata.
 * Dipakai oleh layanan siaga dan layar Beranda.
 * Menangani: buka aplikasi, kirim chat WhatsApp, cari YouTube, tanya AI.
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

    suspend fun proses(ucapan: String): Hasil {
        val bersih = NormalisasiTeks.normalisasi(ucapan)
        if (bersih.isBlank()) return Hasil.Bicara("Tidak terdengar, coba lagi")

        if (bersih == "selesai" || bersih == "terima kasih") {
            return Hasil.Selesai("Sampai jumpa")
        }

        // Pertanyaan lokal tanpa internet: jam dan baterai.
        if (bersih == "jam berapa" || bersih == "pukul berapa" || bersih == "jam sekarang") {
            val jam = java.text.SimpleDateFormat("HH:mm", java.util.Locale("id", "ID"))
                .format(java.util.Date())
            return Hasil.Selesai("Sekarang jam " + jam)
        }
        if (bersih.contains("baterai") && bersih.contains("berapa")) {
            val bm = context.getSystemService(android.os.BatteryManager::class.java)
            val persen = bm?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
            return if (persen >= 0) Hasil.Selesai("Baterai " + persen + " persen")
            else Hasil.Bicara("Level baterai tidak bisa dibaca")
        }

        // Cari di YouTube.
        if (bersih.contains("youtube") && bersih.contains("cari")) {
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
            else Hasil.Bicara("YouTube tidak bisa dibuka")
        }

        // Kirim chat WhatsApp.
        if (bersih.contains("kirim chat") || bersih.contains("kirim pesan") || bersih.contains("kirim wa")) {
            val tujuan = bersih
                .replace("kirim chat", "")
                .replace("kirim pesan", "")
                .replace("kirim wa", "")
                .trim()
            val appWa = peluncur.daftarAplikasi().firstOrNull { it.label.contains("WhatsApp", true) }
            return if (appWa != null && peluncur.buka(appWa)) {
                if (tujuan.isBlank()) Hasil.Selesai("WhatsApp dibuka")
                else Hasil.Selesai("WhatsApp dibuka untuk " + tujuan)
            } else {
                Hasil.Bicara("WhatsApp tidak terpasang")
            }
        }

        // Alias spesifikasi 02.
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
        val label = daftar.map { it.label }
        val cocok = PencocokNama.cariCocok(bersih, label)
        if (cocok.size == 1) {
            val app = daftar.first { it.label == cocok[0] }
            return if (peluncur.buka(app)) Hasil.Selesai("Membuka " + app.label)
            else Hasil.Bicara("Aplikasi tidak ditemukan")
        }
        if (cocok.size > 1) {
            return Hasil.Bicara("Ada beberapa yang mirip: " + cocok.joinToString(", "))
        }

        // Pertanyaan AI.
        val pertanyaan = buangPembuka(bersih)
        val jawab = klien.tanya(pertanyaan)
        return when (jawab) {
            is KlienModel.Hasil.Sukses ->
                Hasil.Selesai(Pembicara.ringkasUntukBaca(jawab.jawaban))
            is KlienModel.Hasil.Gagal -> {
                if (bukaAplikasiAi(context)) {
                    Hasil.Selesai("Belum ada kunci API. Membuka aplikasi AI")
                } else {
                    Hasil.Bicara(jawab.pesan)
                }
            }
        }
    }

    private fun buangPembuka(teks: String): String {
        var hasil = teks
        listOf(
            "tolong berikan jawaban", "berikan jawaban", "tolong carikan tentang",
            "carikan tentang", "tolong carikan", "carikan", "apa itu", "tolong"
        ).forEach { frasa ->
            // Buang hanya frasa utuh agar kata seperti siapa tidak ikut rusak.
            hasil = hasil.replace(Regex("\\b" + Regex.escape(frasa) + "\\b"), "")
        }
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

    companion object {
        fun bukaAplikasiAi(context: Context): Boolean {
            val peluncur = PeluncurAplikasi(context)
            val target = peluncur.daftarAplikasi().firstOrNull {
                it.label.contains("Claude", true) || it.label.contains("ChatGPT", true) ||
                    it.label.contains("GPT", true)
            }
            return target != null && peluncur.buka(target)
        }
    }
}
