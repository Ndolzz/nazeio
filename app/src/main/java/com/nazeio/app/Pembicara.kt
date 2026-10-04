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
        /**
         * Jawaban panjang diringkas saat dibacakan sesuai spesifikasi 03.
         * Jawaban berisi kode tidak dibacakan baris demi baris.
         */
        fun ringkasUntukBaca(teks: String): String {
            val tanpaKode = teks.replace(Regex("```[\s\S]*?```", RegexOption.MULTILINE), " Kode ditampilkan di layar. ")
            return if (tanpaKode.length > 300) {
                tanpaKode.take(280) + " Selengkapnya di layar."
            } else {
                tanpaKode
            }
        }
    }
}
