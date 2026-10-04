package com.nazeio.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Membungkus SpeechRecognizer bawaan Android untuk pengenalan suara
 * bahasa Indonesia. Kandidat lain menyusul setelah uji di HP target.
 */
class PengenalSuara(
    private val context: Context,
    private val padaHasil: (String?) -> Unit,
    private val padaGalat: (String) -> Unit
) : RecognitionListener {

    private val pengenal: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    fun mulai() {
        val niat = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        pengenal.setRecognitionListener(this)
        pengenal.startListening(niat)
    }

    fun berhenti() {
        pengenal.stopListening()
        pengenal.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {
        val pesan = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tidak terdengar, coba lagi"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Izin mikrofon belum diberikan"
            else -> "Pengenalan suara gagal, coba lagi"
        }
        padaGalat(pesan)
    }

    override fun onResults(results: Bundle?) {
        val teks = results
            ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ?.firstOrNull()
        padaHasil(teks)
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}
