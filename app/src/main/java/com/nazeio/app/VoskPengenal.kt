package com.nazeio.app

import android.content.Context
import android.util.Log
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import java.io.File

/**
 * Pengenal suara on-device memakai Vosk dan model Bahasa Indonesia.
 * Cadangan bila SpeechRecognizer Google tidak tersedia di perangkat
 * (umum pada HP ARMv7 tanpa GMS lengkap), sesuai arah spesifikasi 05.
 *
 * Model diambil dari folder berikut di penyimpanan internal aplikasi:
 * /Android/data/com.nazeio.app/files/vosk-model-small-id
 * Unduh "vosk-model-small-id" dari https://alphacephei.com/vosk/models
 * lalu salin foldernya ke lokasi di atas (mis. lewat USB / file manager).
 */
class VoskPengenal(
    private val context: Context,
    private val padaHasil: (String) -> Unit,
    private val padaGalat: (String) -> Unit
) {

    private var model: Model? = null
    private var layanan: SpeechService? = null

    fun mulai() {
        try {
            val m = Model(lokasiModel(context).absolutePath)
            model = m
            val pengenal = Recognizer(m, 16000.0f)
            layanan = SpeechService(pengenal, 16000.0f).also {
                it.startListening(pendengar)
            }
            Log.d(TAG, "mulai: mendengarkan dengan Vosk")
        } catch (e: Exception) {
            Log.e(TAG, "mulai gagal", e)
            padaGalat("model gagal dimuat: ${e.message ?: e.javaClass.simpleName}")
        }
    }

    fun berhenti() {
        try {
            layanan?.stop()
            layanan?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "berhenti gagal", e)
        }
        layanan = null
        try {
            model?.close()
        } catch (e: Exception) {
            Log.e(TAG, "tutup model gagal", e)
        }
        model = null
    }

    private val pendengar = object : org.vosk.android.RecognitionListener {
        override fun onPartialResult(hypothesis: String?) {
            // Hasil parsial diabaikan; cukup hasil akhir tiap jeda bicara.
        }

        override fun onResult(hypothesis: String?) {
            hypothesis?.let { padaHasil(teksDariJson(it)) }
        }

        override fun onFinalResult(hypothesis: String?) {
            hypothesis?.let { padaHasil(teksDariJson(it)) }
        }

        override fun onError(e: Exception?) {
            padaGalat(e?.message ?: "galat tak dikenal")
        }

        override fun onTimeout() {
            // Vosk berhenti sendiri setelah beberapa detik hening; nyalakan lagi.
            berhenti()
            mulai()
        }
    }

    companion object {
        private const val TAG = "VoskPengenal"
        private const val NAMA_MODEL = "vosk-model-small-id"

        /** Lokasi model: folder khusus aplikasi agar tak perlu izin penyimpanan. */
        fun lokasiModel(context: Context): File =
            File(context.getExternalFilesDir(null), NAMA_MODEL)

        /** Benar bila folder model berisi berkas konfigurasi Vosk. */
        fun modelSiap(context: Context): Boolean {
            val dir = lokasiModel(context)
            return dir.isDirectory && dir.listFiles()?.isNotEmpty() == true
        }

        /** Ambil teks dari JSON hasil Vosk: {"text": "..."}. */
        fun teksDariJson(json: String): String = try {
            JSONObject(json).optString("text", "").trim()
        } catch (e: Exception) {
            ""
        }
    }
}
