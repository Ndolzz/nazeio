package com.nazeio.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.nazeio.app.NormalisasiTeks
import com.nazeio.app.PeluncurAplikasi
import com.nazeio.app.PencocokNama
import com.nazeio.app.PengenalSuara

/**
 * Layar Beranda: tombol mikrofon besar, status, dan hasil pemrosesan ucapan.
 * Menerapkan spesifikasi 01: buka aplikasi dengan suara.
 */
@Composable
fun BerandaScreen() {
    val context = LocalContext.current
    var status by remember { mutableStateOf("Tekan mikrofon lalu sebutkan nama aplikasi") }
    var mendengar by remember { mutableStateOf(false) }
    var adaIzin by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val peluncur = remember { PeluncurAplikasi(context) }
    var pengenal by remember { mutableStateOf<PengenalSuara?>(null) }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { diberikan ->
        adaIzin = diberikan
        if (!diberikan) status = "Izin mikrofon dibutuhkan untuk mendengar perintah"
    }

    fun mulaiMendengar() {
        if (!adaIzin) {
            izinLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        val daftar = peluncur.daftarAplikasi()
        pengenal = PengenalSuara(
            context = context,
            padaHasil = { teks ->
                mendengar = false
                val t = teks
                if (t.isNullOrBlank()) {
                    status = "Tidak terdengar, coba lagi"
                } else {
                    val bersih = NormalisasiTeks.normalisasi(t)
                    if (bersih.isBlank()) {
                        status = "Tidak terdengar, coba lagi"
                    } else {
                        val label = daftar.map { it.label }
                        val cocok = PencocokNama.cariCocok(bersih, label)
                        when (cocok.size) {
                            0 -> status = "Aplikasi tidak ditemukan"
                            1 -> {
                                val app = daftar.first { it.label == cocok[0] }
                                if (peluncur.buka(app)) {
                                    status = "Membuka " + app.label
                                } else {
                                    status = "Aplikasi tidak ditemukan"
                                }
                            }
                            else -> {
                                status = "Ada beberapa yang mirip: " +
                                    cocok.joinToString(", ") + ". Mana yang dimaksud?"
                            }
                        }
                    }
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Nazeio",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
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
    }
}
