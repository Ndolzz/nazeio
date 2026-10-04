package com.nazeio.app

import com.nazeio.app.data.Pengaturan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Klien API model bahasa sesuai spesifikasi 03.
 * Mendukung Gemini dan Claude. Jawaban singkat dan berbahasa Indonesia
 * sesuai instruksi sistem.
 */
class KlienModel(private val pengaturan: Pengaturan) {

    sealed class Hasil {
        data class Sukses(val jawaban: String) : Hasil()
        data class Gagal(val pesan: String) : Hasil()
    }

    suspend fun tanya(pertanyaan: String): Hasil = withContext(Dispatchers.IO) {
        val kunci = pengaturan.kunciApi
        if (kunci.isBlank()) return@withContext Hasil.Gagal("Kunci API belum diatur di Pengaturan")
        if (pengaturan.pemakaianHari >= pengaturan.batasHarian) {
            return@withContext Hasil.Gagal("Batas pemakaian harian sudah tercapai")
        }
        try {
            val jawaban = when (pengaturan.penyediaApi) {
                "claude" -> tanyaClaude(kunci, pertanyaan)
                else -> tanyaGemini(kunci, pertanyaan)
            }
            pengaturan.pemakaianHari = pengaturan.pemakaianHari + 1
            Hasil.Sukses(jawaban)
        } catch (e: KoneksiGagal) {
            Hasil.Gagal(e.pesan)
        } catch (e: Exception) {
            Hasil.Gagal("Permintaan ke model gagal, coba lagi")
        }
    }

    private class KoneksiGagal(val pesan: String) : Exception(pesan)

    private fun tanyaGemini(kunci: String, pertanyaan: String): String {
        val url = URL(
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
        )
        val isi = JSONObject().put("contents", org.json.JSONArray().put(
            JSONObject().put("parts", org.json.JSONArray().put(
                JSONObject().put("text", INSTRUKSI + pertanyaan)
            ))
        ))
        val respon = kirim(
            url, "POST", isi.toString(),
            tambahan = mapOf("x-goog-api-key" to kunci)
        )
        val teks = respon
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
        return teks
    }

    private fun tanyaClaude(kunci: String, pertanyaan: String): String {
        val url = URL("https://api.anthropic.com/v1/messages")
        val isi = JSONObject()
            .put("model", "claude-sonnet-4-5")
            .put("max_tokens", 1024)
            .put("system", INSTRUKSI)
            .put("messages", org.json.JSONArray().put(
                JSONObject().put("role", "user").put("content", pertanyaan)
            ))
        val respon = kirim(
            url, "POST", isi.toString(),
            tambahan = mapOf(
                "x-api-key" to kunci,
                "anthropic-version" to "2023-06-01"
            )
        )
        return respon.getJSONArray("content").getJSONObject(0).getString("text")
    }

    private fun kirim(
        url: URL,
        metode: String,
        body: String,
        tambahan: Map<String, String> = emptyMap()
    ): JSONObject {
        val koneksi = url.openConnection() as HttpURLConnection
        koneksi.requestMethod = metode
        koneksi.connectTimeout = 30000
        koneksi.readTimeout = 60000
        koneksi.setRequestProperty("Content-Type", "application/json")
        tambahan.forEach { (k, v) -> koneksi.setRequestProperty(k, v) }
        koneksi.doOutput = true
        koneksi.outputStream.use { it.write(body.toByteArray()) }
        val kode = koneksi.responseCode
        val masukan = if (kode in 200..299) koneksi.inputStream else koneksi.errorStream
        val teks = BufferedReader(InputStreamReader(masukan)).readText()
        koneksi.disconnect()
        if (kode !in 200..299) {
            throw KoneksiGagal("API menjawab kode " + kode)
        }
        return JSONObject(teks)
    }

    companion object {
        const val INSTRUKSI =
            "Kamu adalah Nazeio, asisten suara pribadi di HP pengguna. " +
            "Jawab singkat, langsung, dan dalam bahasa Indonesia. " +
            "Kalau jawaban berisi kode, tulis kodenya dalam blok kode. "
    }
}
