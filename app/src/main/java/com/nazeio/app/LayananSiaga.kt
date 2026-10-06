package com.nazeio.app

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
import android.util.Log
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

    // Prioritas awal: pengenal offline agar hemat kuota dan cepat.
    // Bila offline ternyata tidak tersedia atau terus gagal, beralih ke online.
    private var pakaiOffline = true

    // Diagnostik sementara: tampilkan di layar apa yang didengar dan galat apa
    // yang muncul. Dimatikan setelah penyebab "Nazeio tidak mendengar" ketemu.
    private var galatTerakhirToast = 0L

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
        aktif = true
        Log.d(TAG, "onCreate")
        pemroses = PemrosesPerintah(this)
        penggerak = PenggerakWidget(this)
        tts = TextToSpeech(this) { hasil ->
            Log.d(TAG, "TTS init: hasil=$hasil")
            ttsSiap = hasil == TextToSpeech.SUCCESS
            if (!ttsSiap) {
                handler.post {
                    Toast.makeText(
                        this,
                        "TTS gagal dimulai (kode $hasil). Nazeio tidak bisa bersuara.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@TextToSpeech
            }
            val id = Locale("id", "ID")
            val setLocale = tts?.setLanguage(id)
            Log.d(TAG, "TTS setLanguage id-ID: $setLocale (0=OK, -1=missing, -2=not supported)")
            tts?.setOnUtteranceProgressListener(pendengarTts)
            // Voice data Bahasa Indonesia belum terpasang: ingatkan pengguna
            // agar Nazeio tidak menjawab dengan suara asing atau diam saja.
            val tersedia = tts?.isLanguageAvailable(id) ?: TextToSpeech.LANG_NOT_SUPPORTED
            Log.d(TAG, "TTS isLanguageAvailable: $tersedia")
            if (tersedia == TextToSpeech.LANG_MISSING_DATA ||
                tersedia == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                handler.post {
                    Toast.makeText(
                        this,
                        "Suara Bahasa Indonesia belum terpasang (kode $tersedia). Buka Pengaturan > TTS dan pasang voice data Bahasa Indonesia.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        // Isi tabel alias awal agar perintah suara berfungsi walau aplikasi belum dibuka.
        scope.launch(Dispatchers.IO) { RepositoriAlias(this@LayananSiaga).pastikanDataAwal() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: aksi=${intent?.action}")
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
        Log.d(TAG, "onDestroy")
        berjalan = false
        percakapan = false
        aktif = false
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
        Log.e(TAG, "startForeground ditolak", e)
        // Izin mikrofon belum ada, atau sistem menolak layanan latar depan.
        // Jangan gagal diam-diam: beri tahu pengguna penyebab paling umum.
        handler.post {
            Toast.makeText(
                this,
                "Nazeio belum bisa mendengar. Pastikan izin mikrofon sudah diberikan, lalu nyalakan siaga dari dalam aplikasi.",
                Toast.LENGTH_LONG
            ).show()
        }
        StatusBersama.set(this, Status.MATI)
        PembaruWidget.penuh(this)
        stopSelf()
        false
    }

    private fun mulaiLayanan() {
        if (berjalan) return
        berjalan = true
        pakaiOffline = true
        galatBerturut = 0
        Log.d(TAG, "mulaiLayanan: siaga menyala")
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
            Log.e(TAG, "bicara dibatalkan: ttsSiap=false, pesan=$pesan")
            selesaiBicara()
            return
        }
        sedangBicara = true
        handler.removeCallbacks(ulangiMendengar)
        pengenal?.destroy()
        pengenal = null
        nomorUcapan++
        Log.d(TAG, "bicara: $pesan")
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

    private fun niatSuara(offline: Boolean) = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
        if (offline) putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
    }

    private fun mulaiMendengar() {
        if (!berjalan || sedangBicara) return
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e(TAG, "mulaiMendengar: pengenal suara tidak tersedia")
            Toast.makeText(this, "Pengenal suara tidak tersedia di perangkat ini", Toast.LENGTH_LONG).show()
            matikan()
            return
        }
        pengenal?.destroy()
        pengenal = SpeechRecognizer.createSpeechRecognizer(this).also {
            it.setRecognitionListener(pendengar)
            Log.d(TAG, "mulaiMendengar: mulai, offline=$pakaiOffline")
            it.startListening(niatSuara(pakaiOffline))
        }
    }

    private fun jedaLalu(ms: Long) {
        handler.removeCallbacks(ulangiMendengar)
        handler.postDelayed(ulangiMendengar, ms)
    }

    /** Toast galat dibatasi satu per 2 detik agar layar tidak banjir. */
    private fun toastGalat(pesan: String) {
        val kini = System.currentTimeMillis()
        if (kini - galatTerakhirToast < 2000) return
        galatTerakhirToast = kini
        handler.post { Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show() }
    }

    private val pendengar = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "onReadyForSpeech (mikrofon aktif)")
        }
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
            Log.d(TAG, "onResults: terdengar='$teks' (percakapan=$percakapan)")
            if (!percakapan) toastGalat("Terdengar: $teks")
            prosesUcapan(teks)
            if (!sedangBicara) mulaiMendengar()
        }

        /** Bila pengenal offline terus gagal atau butuh jaringan, beralih ke online. */
        private fun beralihOnlineBilaPerlu(): Boolean {
            if (pakaiOffline) {
                pakaiOffline = false
                galatBerturut = 0
                Log.d(TAG, "beralih ke pengenal online")
                toastGalat("Model offline gagal, beralih ke online")
                jedaLalu(150)
                return true
            }
            return false
        }

        override fun onError(error: Int) {
            if (!berjalan || sedangBicara) return
            Log.e(TAG, "onError: kode=$error (7=tiada mikrofon, 6=ucapan tiada, 1=jaringan, 2=jaringan lambat, 3=audio, 4=server, 5=sangat buruk, 8=sibuk, 9=rendah, 10=izin)")
            when (error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    Toast.makeText(
                        this@LayananSiaga,
                        "Mikrofon diblokir. Buka aplikasi Nazeio, beri izin mikrofon, lalu nyalakan siaga dari dalam aplikasi.",
                        Toast.LENGTH_LONG
                    ).show()
                    matikan()
                }
                SpeechRecognizer.ERROR_NO_MATCH,
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> jedaLalu(150)
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> jedaLalu(1000)
                SpeechRecognizer.ERROR_AUDIO -> {
                    // Galat audio sering berarti mikrofon dipakai proses lain.
                    toastGalat("Galat audio (3): mikrofon mungkin dipakai aplikasi lain")
                    jedaLalu(1000)
                }
                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                SpeechRecognizer.ERROR_SERVER,
                SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED,
                SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> {
                    toastGalat("Galat pengenal ($error), mencoba mode lain...")
                    if (!beralihOnlineBilaPerlu()) jedaLalu(1000)
                }
                else -> {
                    galatBerturut++
                    toastGalat("Galat pengenal suara kode $error")
                    // Tiga galat berturut-turut saat mode offline: coba mode online.
                    if (galatBerturut >= 3 && beralihOnlineBilaPerlu()) return
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
        private const val TAG = "NazeioSiaga"
        private const val BATAS_DIAM_MS = 8000L

        /**
         * Penanda hidup proses layanan, lebih andal daripada getRunningServices
         * yang sudah deprecated dan sering salah lapor.
         */
        @Volatile
        var aktif: Boolean = false

        fun sedangJalan(context: Context): Boolean = aktif
    }
}
