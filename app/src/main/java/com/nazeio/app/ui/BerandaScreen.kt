package com.nazeio.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nazeio.app.AksiPengaturan
import com.nazeio.app.KlienModel
import com.nazeio.app.LayananSiaga
import com.nazeio.app.NormalisasiTeks
import com.nazeio.app.PeluncurAplikasi
import com.nazeio.app.Pembicara
import com.nazeio.app.PencocokNama
import com.nazeio.app.PengenalSuara
import com.nazeio.app.data.Pengaturan
import com.nazeio.app.data.RepositoriAlias
import kotlinx.coroutines.launch

/**
 * Layar Beranda: tombol mikrofon, sakelar mode siaga, dan status.
 * Sesuai spesifikasi 01, 02, 03, dan 05.
 */
@Composable
fun BerandaScreen(
    bukaLayarAlias: () -> Unit,
    bukaLayarPengaturan: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Tekan mikrofon lalu bicara") }
    var jawaban by remember { mutableStateOf("") }
    var mendengar by remember { mutableStateOf(false) }
    var siaga by remember { mutableStateOf(false) }
    var adaIzin by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val peluncur = remember { PeluncurAplikasi(context) }
    val repositori = remember { RepositoriAlias(context) }
    val pengaturan = remember { Pengaturan(context) }
    val klien = remember { KlienModel(pengaturan) }
    val pembicara = remember { Pembicara(context) }
    var pengenal by remember { mutableStateOf<PengenalSuara?>(null) }

    DisposableEffect(Unit) {
        onDispose { pembicara.tutup() }
    }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { hasil ->
        adaIzin = hasil[Manifest.permission.RECORD_AUDIO] == true
        if (adaIzin && siaga) nyalakanSiaga(context)
    }

    fun prosesTeks(t: String) {
        val bersih = NormalisasiTeks.normalisasi(t)
        if (bersih.isBlank()) {
            status = "Tidak terdengar, coba lagi"
            return
        }
        scope.launch {
            val aksiCocok = repositori.aksiUntukAlias(bersih)
            when {
                aksiCocok.size == 1 -> {
                    val nama = aksiCocok[0]
                    if (AksiPengaturan.buka(context, nama)) {
                        status = "Membuka " + nama.replace("panel_", "").replace("_", " ")
                    } else {
                        status = "Aksi belum bisa dijalankan di perangkat ini"
                    }
                }
                aksiCocok.size > 1 -> {
                    status = "Ada beberapa yang mirip. Mana yang dimaksud?"
                }
                else -> {
                    val daftar = peluncur.daftarAplikasi()
                    val cocok = PencocokNama.cariCocok(bersih, daftar.map { it.label })
                    when (cocok.size) {
                        1 -> {
                            val app = daftar.first { it.label == cocok[0] }
                            if (peluncur.buka(app)) status = "Membuka " + app.label
                            else status = "Aplikasi tidak ditemukan"
                        }
                        0 -> {
                            status = "Berpikir"
                            jawaban = ""
                            when (val hasil = klien.tanya(bersih)) {
                                is KlienModel.Hasil.Sukses -> {
                                    jawaban = hasil.jawaban
                                    status = "Jawaban Nazeio"
                                    pembicara.bicara(Pembicara.ringkasUntukBaca(hasil.jawaban))
                                }
                                is KlienModel.Hasil.Gagal -> status = hasil.pesan
                            }
                        }
                        else -> status = "Ada beberapa yang mirip. Mana yang dimaksud?"
                    }
                }
            }
        }
    }

    fun mulaiMendengar() {
        if (!adaIzin) {
            izinLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
            return
        }
        pembicara.hentikan()
        scope.launch { repositori.pastikanDataAwal() }
        pengenal = PengenalSuara(
            context = context,
            padaHasil = { teks ->
                mendengar = false
                if (teks.isNullOrBlank()) {
                    status = "Tidak terdengar, coba lagi"
                } else {
                    prosesTeks(teks)
                }
            },
            padaGalat = { pesan ->
                mendengar = false
                status = pesan
            }
        )
        mendengar = true
        status = "Mendengar"
        pengenal?.mulai()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Nazeio", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "Asisten suara pribadi",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Box(
            modifier = Modifier
                .padding(top = 48.dp)
                .size(96.dp)
                .background(
                    if (mendengar) Color(0xFF7C3AED) else Color(0xFF2563EB),
                    CircleShape
                )
                .clickableTanpaRiak {
                    if (mendengar) {
                        pengenal?.berhenti()
                        pembicara.hentikan()
                        mendengar = false
                        status = "Dibatalkan"
                    } else {
                        mulaiMendengar()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (mendengar) "Stop" else "Dengar",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = status,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 32.dp)
        )

        if (jawaban.isNotEmpty()) {
            Text(
                text = jawaban,
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mode siaga: katakan Nazeio kapan saja",
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = siaga,
                onCheckedChange = { nyala ->
                    siaga = nyala
                    if (nyala) {
                        if (!adaIzin) {
                            izinLauncher.launch(
                                arrayOf(
                                    Manifest.permission.RECORD_AUDIO,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            )
                        } else {
                            nyalakanSiaga(context)
                        }
                    } else {
                        context.stopService(Intent(context, LayananSiaga::class.java))
                    }
                }
            )
        }

        Row(modifier = Modifier.padding(top = 16.dp)) {
            TextButton(onClick = bukaLayarAlias) { Text("Alias") }
            TextButton(onClick = bukaLayarPengaturan) { Text("Pengaturan") }
            if (pembicara.sedangBicara) {
                TextButton(onClick = { pembicara.hentikan() }) { Text("Hentikan suara") }
            }
        }

        if (pengaturan.pemakaianHampirHabis) {
            Text(
                text = "Batas harian hampir tercapai.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun nyalakanSiaga(context: Context) {
    val niat = Intent(context, LayananSiaga::class.java)
    context.startForegroundService(niat)
}
