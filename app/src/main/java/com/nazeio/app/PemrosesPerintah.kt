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

    /** Nama aplikasi yang menunggu jawaban konfirmasi pengguna. */
    private var konfirmasiTertunda: String? = null

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

        // Jawaban atas konfirmasi kecocokan aplikasi yang belum yakin.
        val tertunda = konfirmasiTertunda
        if (tertunda != null) {
            konfirmasiTertunda = null
            if (bersih == "iya" || bersih == "ya" || bersih == "benar" || bersih == "buka") {
                val app = peluncur.daftarAplikasi().firstOrNull { it.label == tertunda }
                return if (app != null && peluncur.buka(app)) Hasil.Selesai("Membuka " + app.label)
                else Hasil.Bicara("Aplikasi tidak ditemukan")
            }
            // Bukan jawaban konfirmasi, lanjut memproses ucapan baru.
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

        // Pengingat.
        if (bersih.contains("ingatkan")) {
            val waktu = PengingatWaktu.dariUcapan(bersih, System.currentTimeMillis())
            if (waktu == null) return Hasil.Bicara("Kapan pengingatnya. Contoh, ingatkan aku lima menit lagi")
            return if (pasangPengingat(waktu)) Hasil.Selesai("Baik, saya ingatkan")
            else Hasil.Bicara("Pengingat gagal dipasang")
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

        // Telepon sesuai spesifikasi 08.
        if (bersih == "telepon" || bersih.startsWith("telepon ") ||
            bersih == "panggil" || bersih.startsWith("panggil ")
        ) {
            val tujuan = bersih.removePrefix("telepon").removePrefix("panggil").trim()
            if (tujuan.isBlank()) return Hasil.Bicara("Mau menelepon siapa")
            val pemanggil = PemanggilTelepon(context)
            // Nomor langsung, misalnya telepon 08123456789.
            if (Regex("^\\d{3,}$").matches(tujuan)) {
                return if (pemanggil.bukaDialer(tujuan)) Hasil.Selesai("Membuka dialer")
                else Hasil.Bicara("Dialer tidak bisa dibuka")
            }
            if (!pemanggil.izinKontak()) {
                return Hasil.Bicara("Izin kontak belum diberikan. Buka aplikasi Nazeio lalu berikan izin kontak")
            }
            val kontak = pemanggil.cariKontak(tujuan)
            if (kontak.isEmpty()) return Hasil.Bicara("Kontak tidak ditemukan")
            if (kontak.size > 1) {
                return Hasil.Bicara(
                    "Ada beberapa yang mirip: " + kontak.take(3).joinToString(", ") { it.nama }
                )
            }
            return if (pemanggil.bukaDialer(kontak[0].nomor)) {
                Hasil.Selesai("Membuka dialer untuk " + kontak[0].nama)
            } else {
                Hasil.Bicara("Dialer tidak bisa dibuka")
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
        val cocok = PencocokNama.cariSkor(bersih, label)
        if (cocok.size == 1) {
            val (nama, skor) = cocok[0]
            if (skor < 0.9) {
                // Kecocokan di bawah 0,9 wajib dikonfirmasi lisan lebih dulu.
                konfirmasiTertunda = nama
                return Hasil.Bicara("Kamu maksud " + nama + ". Sebut iya untuk membuka")
            }
            val app = daftar.first { it.label == nama }
            return if (peluncur.buka(app)) Hasil.Selesai("Membuka " + app.label)
            else Hasil.Bicara("Aplikasi tidak ditemukan")
        }
        if (cocok.size > 1) {
            return Hasil.Bicara("Ada beberapa yang mirip: " + cocok.joinToString(", ") { it.first })
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

    private fun pasangPengingat(waktu: Long): Boolean {
        return try {
            val am = context.getSystemService(android.app.AlarmManager::class.java) ?: return false
            val kode = (waktu / 1000).toInt()
            val niat = Intent(context, PenerimaPengingat::class.java)
                .putExtra(PenerimaPengingat.EXTRA_ID, kode)
            val pi = android.app.PendingIntent.getBroadcast(
                context, kode, niat,
                android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
            )
            am.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, waktu, pi)
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
