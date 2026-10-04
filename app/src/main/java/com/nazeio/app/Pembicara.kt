package com.nazeio.app

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Membacakan jawaban sesuai spesifikasi 03.
 * Sementara memakai TTS bawaan Android. Piper id_ID menyusul
 * setelah uji di HP target sesuai catatan spesifikasi induk.
 */
class Pembicara(context: Context) {

    private var tts: TextToSpeech? = null
    private var siap = false

    val sedangBicara: Boolean
        get() = tts?.isSpeaking == true

    init {
        tts = TextToSpeech(context) { hasil ->
            siap = hasil == TextToSpeech.SUCCESS
            tts?.language = Locale("id", "ID")
        }
    }

    fun bicara(teks: String) {
        if (!siap) return
        tts?.speak(teks, TextToSpeech.QUEUE_FLUSH, null, "nazeio")
    }

    fun hentikan() {
        tts?.stop()
    }

    fun tutup() {
        tts?.stop()
        tts?.shutdown()
    }

    companion object {
        private val PENANDA_KODE = String(charArrayOf(96.toChar(), 96.toChar(), 96.toChar()))

        /**
         * Jawaban panjang diringkas saat dibacakan sesuai spesifikasi 03.
         * Jawaban berisi kode tidak dibacakan baris demi baris.
         */
        fun ringkasUntukBaca(teks: String): String {
            val tanpaKode = if (teks.contains(PENANDA_KODE)) {
                val bagian = teks.split(PENANDA_KODE)
                val teksSaja = bagian.filterIndexed { i, _ -> i % 2 == 0 }
                    .joinToString(" ")
                    .trim()
                if (teksSaja.isBlank()) "Kode ditampilkan di layar."
                else teksSaja + " Kode ditampilkan di layar."
            } else {
                teks
            }
            return if (tanpaKode.length > 300) {
                tanpaKode.take(280) + " Selengkapnya di layar."
            } else {
                tanpaKode
            }
        }
    }
}
