package com.nazeio.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Layanan siaga sesuai spesifikasi 05.
 * Mendengarkan terus di latar belakang menunggu kata pemicu Nazeio,
 * lalu menjawab "Nazeio aktif" dan memproses perintah berikutnya
 * sampai percakapan berakhir.
 */
class LayananSiaga : Service() {

    private var tts: TextToSpeech? = null
    private var siap = false
    private var pengenal: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var pemroses: PemrosesPerintah
    private var sedangPercakapan = false

    private val akhiriPercakapan = Runnable {
        if (sedangPercakapan) {
            sedangPercakapan = false
            bicara("Percakapan selesai")
        }
    }

    override fun onCreate() {
        super.onCreate()
        pemroses = PemrosesPerintah(this)
        tts = TextToSpeech(this) { hasil ->
            siap = hasil == TextToSpeech.SUCCESS
            tts?.language = Locale("id", "ID")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buatKanal()
        startForeground(ID_NOTIF, notifikasi())
        mulaiMendengar()
        return START_STICKY
    }

    override fun onDestroy() {
        pengenal?.destroy()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun mulaiMendengar() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return
        pengenal?.destroy()
        pengenal = SpeechRecognizer.createSpeechRecognizer(this)
        pengenal?.setRecognitionListener(pendengar)
        val niat = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        pengenal?.startListening(niat)
    }

    private val pendengar = object : RecognitionListener {
        override fun onReadyForSpeech(params: android.os.Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        override fun onPartialResults(partialResults: android.os.Bundle?) {}

        override fun onError(error: Int) {
            // Galat umum seperti batas waktu atau tidak terdengar: ulangi.
            handler.postDelayed({ mulaiMendengar() }, 300)
        }

        override fun onResults(results: android.os.Bundle?) {
            val teks = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.lowercase() ?: ""

            if (sedangPercakapan) {
                if (teks.isNotBlank()) prosesPerintah(teks)
            } else if (teks.contains("nazeio")) {
                sedangPercakapan = true
                bicara("Nazeio aktif")
            }
            handler.postDelayed(akhiriPercakapan, 8000)
            mulaiMendengar()
        }
    }

    private fun prosesPerintah(ucapan: String) {
        scope.launch {
            when (val hasil = pemroses.proses(ucapan)) {
                is PemrosesPerintah.Hasil.Selesai -> {
                    bicara(hasil.pesan)
                    sedangPercakapan = false
                }
                is PemrosesPerintah.Hasil.Bicara -> bicara(hasil.pesan)
            }
        }
    }

    private fun bicara(pesan: String) {
        if (siap) tts?.speak(pesan, TextToSpeech.QUEUE_FLUSH, null, "nazeio")
    }

    private fun buatKanal() {
        val kanal = NotificationChannel(
            KANAL, "Mode siaga Nazeio", NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(kanal)
    }

    private fun notifikasi(): Notification {
        val buang = Intent(this, LayananSiaga::class.java).setAction(AKSI_MATIKAN)
        val buangPi = android.app.PendingIntent.getService(
            this, 1, buang,
            android.app.PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(this, KANAL)
            .setContentTitle("Nazeio siaga")
            .setContentText("Mendengarkan kata pemicu Nazeio")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .addAction(
                android.R.drawable.ic_delete,
                "Matikan",
                buangPi
            )
            .setOngoing(true)
            .build()
    }

    companion object {
        const val KANAL = "siaga"
        const val ID_NOTIF = 1
        const val AKSI_MATIKAN = "matikan"
    }
}
