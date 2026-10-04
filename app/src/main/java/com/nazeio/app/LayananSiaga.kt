package com.nazeio.app

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import com.nazeio.app.data.RepositoriAlias
import com.nazeio.app.widget.PembaruWidget
import com.nazeio.app.widget.PenggerakWidget
import com.nazeio.app.widget.StatusBersama
import com.nazeio.app.widget.StatusBersama.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Layanan siaga sesuai spesifikasi 05 dan 06.
 *
 * Alur status: MATI, SIAGA (menunggu kata pemicu), AKTIF (percakapan).
 * Percakapan berlanjut sampai pengguna diam 8 detik, berkata "selesai",
 * atau menekan tombol berhenti. Pendengar dijeda selama Nazeio berbicara
 * agar suaranya sendiri tidak terdengar sebagai perintah.
 *
 * Aksi intent:
 * nyalakan   mulai siaga
 * percakapan mulai percakapan langsung (dari mikrofon widget)
 * akhiri     akhiri percakapan, kembali siaga
 * matikan    hentikan layanan
 */
class LayananSiaga : Service() {

    private var tts: TextToSpeech? = null
    private var ttsSiap = false
    private var nomorUcapan = 0
    private var pengenal: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var pemroses: PemrosesPerintah
    private lateinit var penggerak: PenggerakWidget

    private var berjalan = false
    private var percakapan = false
    private var sedangBicara = false
    private var hanyaPercakapan = false
    private var matikanSetelahBicara = false
    private var galatBerturut = 0

    private val batasDiam = Runnable {
        if (percakapan) akhiriPercakapan("Percakapan selesai")
    }

    private val ulangiMendengar = Runnable { mulaiMendengar() }

    private val pendengarTts = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {}

        override fun onDone(utteranceId: String?) {
            if (utteranceId == nomorUcapan.toString()) handler.post { selesaiBicara() }
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
            if (utteranceId == nomorUcapan.toString()) handler.post { selesaiBicara() }
        }
    }

    override fun onCreate() {
        super.onCreate()
        pemroses = PemrosesPerintah(this)
        penggerak = PenggerakWidget(this)
        tts = TextToSpeech(this) { hasil ->
            ttsSiap = hasil == TextToSpeech.SUCCESS
            if (ttsSiap) {
                tts?.language = Locale("id", "ID")
                tts?.setOnUtteranceProgressListener(pendengarTts)
            }
        }
        // Isi tabel alias awal agar perintah suara berfungsi walau aplikasi belum dibuka.
        scope.launch(Dispatchers.IO) { RepositoriAlias(this@LayananSiaga).pastikanDataAwal() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buatKanal()
        if (!masukLatarDepan()) return START_NOT_STICKY
        when (intent?.action) {
            AKSI_MATIKAN -> {
                matikan()
                return START_NOT_STICKY
            }
            AKSI_AKHIRI -> {
                if (percakapan) akhiriPercakapan("Percakapan selesai") else if (!berjalan) matikan()
            }
            AKSI_PERCAKAPAN -> {
                if (!berjalan) hanyaPercakapan = true
                mulaiLayanan()
                mulaiPercakapan("Ya")
            }
            else -> {
                hanyaPercakapan = false
                mulaiLayanan()
            }
        }
        return if (hanyaPercakapan) START_NOT_STICKY else START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        berjalan = false
        percakapan = false
        handler.removeCallbacksAndMessages(null)
        penggerak.berhenti()
        scope.cancel()
        pengenal?.destroy()
        pengenal = null
        tts?.shutdown()
        tts = null
        StatusBersama.set(this, Status.MATI)
        PembaruWidget.penuh(this)
        super.onDestroy()
    }

    // Siklus layanan

    private fun masukLatarDepan(): Boolean = try {
        startForeground(ID_NOTIF, notifikasi())
        true
    } catch (e: RuntimeException) {
        // Izin mikrofon belum ada, atau sistem menolak layanan latar depan.
        StatusBersama.set(this, Status.MATI)
        PembaruWidget.penuh(this)
        stopSelf()
        false
    }

    private fun mulaiLayanan() {
        if (berjalan) return
        berjalan = true
        StatusBersama.set(this, Status.SIAGA)
        PembaruWidget.penuh(this)
        penggerak.mulai()
        mulaiMendengar()
    }

    private fun matikan() {
        berjalan = false
        percakapan = false
        handler.removeCallbacksAndMessages(null)
        penggerak.berhenti()
        pengenal?.destroy()
        pengenal = null
        tts?.stop()
        StatusBersama.set(this, Status.MATI)
        PembaruWidget.penuh(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    // Percakapan

    private fun mulaiPercakapan(sapaan: String?) {
        percakapan = true
        galatBerturut = 0
        StatusBersama.set(this, Status.AKTIF)
        PembaruWidget.penuh(this)
        PembaruWidget.ucapan(this, "Silakan bicara")
        aturPewaktu()
        if (sapaan != null) bicara(sapaan)
    }

    private fun akhiriPercakapan(pesan: String?) {
        handler.removeCallbacks(batasDiam)
        percakapan = false
        if (hanyaPercakapan) {
            matikanSetelahBicara = true
        } else {
            StatusBersama.set(this, Status.SIAGA)
            PembaruWidget.penuh(this)
        }
        if (pesan != null) bicara(pesan) else if (matikanSetelahBicara) matikan()
    }

    /** Pewaktu diam 8 detik. Selalu dibatalkan dulu agar tidak menumpuk. */
    private fun aturPewaktu() {
        handler.removeCallbacks(batasDiam)
        if (percakapan) handler.postDelayed(batasDiam, BATAS_DIAM_MS)
    }

    private fun prosesUcapan(teks: String) {
        if (teks.isBlank()) return
        if (!percakapan) {
            val pemicu = PemicuKata.cari(teks)
            if (!pemicu.ada) return
            val ada = pemicu.sisa.isNotBlank()
            mulaiPercakapan(if (ada) null else "Ya")
            if (ada) prosesPerintah(pemicu.sisa)
            return
        }
        aturPewaktu()
        PembaruWidget.ucapan(this, teks)
        if (PemicuKata.akhiri(teks)) {
            akhiriPercakapan("Sampai jumpa")
            return
        }
        prosesPerintah(teks)
    }

    private fun prosesPerintah(ucapan: String) {
        scope.launch {
            val pesan = try {
                when (val hasil = pemroses.proses(ucapan)) {
                    is PemrosesPerintah.Hasil.Selesai -> hasil.pesan
                    is PemrosesPerintah.Hasil.Bicara -> hasil.pesan
                }
            } catch (e: Exception) {
                "Terjadi galat, coba lagi"
            }
            Riwayat.tambah(ucapan.take(30), pesan.take(40))
            if (berjalan) bicara(pesan)
        }
    }

    // Suara keluar

    private fun bicara(pesan: String) {
        handler.removeCallbacks(batasDiam)
        if (!ttsSiap) {
            selesaiBicara()
            return
        }
        sedangBicara = true
        handler.removeCallbacks(ulangiMendengar)
        pengenal?.destroy()
        pengenal = null
        nomorUcapan++
        tts?.speak(pesan, TextToSpeech.QUEUE_FLUSH, null, nomorUcapan.toString())
    }

    private fun selesaiBicara() {
        sedangBicara = false
        if (!berjalan) return
        if (matikanSetelahBicara) {
            matikan()
            return
        }
        aturPewaktu()
        mulaiMendengar()
    }

    // Suara masuk

    private fun niatSuara() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
    }

    private fun mulaiMendengar() {
        if (!berjalan || sedangBicara) return
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Pengenal suara tidak tersedia di perangkat ini", Toast.LENGTH_LONG).show()
            matikan()
            return
        }
        pengenal?.destroy()
        pengenal = SpeechRecognizer.createSpeechRecognizer(this).also {
            it.setRecognitionListener(pendengar)
            it.startListening(niatSuara())
        }
    }

    private fun jedaLalu(ms: Long) {
        handler.removeCallbacks(ulangiMendengar)
        handler.postDelayed(ulangiMendengar, ms)
    }

    private val pendengar = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onPartialResults(partialResults: Bundle?) {
            if (!percakapan || sedangBicara) return
            val teks = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull().orEmpty()
            if (teks.isNotBlank()) {
                aturPewaktu()
                PembaruWidget.ucapan(this@LayananSiaga, teks)
            }
        }

        override fun onResults(results: Bundle?) {
            galatBerturut = 0
            val teks = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull().orEmpty()
            prosesUcapan(teks)
            if (!sedangBicara) mulaiMendengar()
        }

        override fun onError(error: Int) {
            if (!berjalan || sedangBicara) return
            when (error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    Toast.makeText(
                        this@LayananSiaga, "Izin mikrofon belum diberikan", Toast.LENGTH_LONG
                    ).show()
                    matikan()
                }
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> jedaLalu(150)
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> jedaLalu(1000)
                else -> {
                    galatBerturut++
                    jedaLalu(if (galatBerturut > 5) 3000 else 400)
                }
            }
        }
    }

    // Notifikasi

    private fun buatKanal() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(KANAL, "Mode siaga Nazeio", NotificationManager.IMPORTANCE_LOW)
        )
    }

    @Suppress("DEPRECATION")
    private fun notifikasi(): Notification {
        val flag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        val buka = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), flag)
        val matikan = PendingIntent.getService(
            this, 1, Intent(this, LayananSiaga::class.java).setAction(AKSI_MATIKAN), flag
        )
        return Notification.Builder(this, KANAL)
            .setContentTitle("Nazeio siaga")
            .setContentText("Ucapkan Nazeio kapan saja")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(buka)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Matikan", matikan)
            .build()
    }

    companion object {
        const val KANAL = "siaga"
        const val ID_NOTIF = 1
        const val AKSI_NYALAKAN = "nyalakan"
        const val AKSI_MATIKAN = "matikan"
        const val AKSI_PERCAKAPAN = "percakapan"
        const val AKSI_AKHIRI = "akhiri"
        private const val BATAS_DIAM_MS = 8000L

        @Suppress("DEPRECATION")
        fun sedangJalan(context: Context): Boolean {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return am.getRunningServices(Int.MAX_VALUE)
                .any { it.service.className == LayananSiaga::class.java.name }
        }
    }
}
