package com.nazeio.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nazeio.app.AksiPengaturan
import com.nazeio.app.KlienModel
import com.nazeio.app.ControlLayanan
import com.nazeio.app.LayananSiaga
import com.nazeio.app.NormalisasiTeks
import com.nazeio.app.PeluncurAplikasi
import com.nazeio.app.Pembicara
import com.nazeio.app.PencocokNama
import com.nazeio.app.PengenalSuara
import com.nazeio.app.Riwayat
import com.nazeio.app.data.Pengaturan
import com.nazeio.app.data.RepositoriAlias
import com.nazeio.app.widget.StatusBersama
import kotlinx.coroutines.launch

/**
 * Layar Beranda sesuai desain: bar atas, orb mikrofon,
 * baris mode siaga, dan bagian TERAKHIR.
 */
@Composable
fun BerandaScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Siap") }
    var jawaban by remember { mutableStateOf("") }
    var mendengar by remember { mutableStateOf(false) }
    // Status siaga dibagi dengan layanan, widget, dan layar Pengaturan.
    val siaga = StatusBersama.status != StatusBersama.Status.MATI
    var mauSiaga by remember { mutableStateOf(false) }
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
        if (adaIzin && mauSiaga) ControlLayanan.nyalakan(context)
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
                        Riwayat.tambah(status, "Berhasil")
                    } else {
                        status = "Aksi belum bisa dijalankan di perangkat ini"
                    }
                }
                aksiCocok.size > 1 -> {
                    status = "Ada beberapa yang mirip. Mana yang dimaksud"
                }
                else -> {
                    val daftar = peluncur.daftarAplikasi()
                    val cocok = PencocokNama.cariCocok(bersih, daftar.map { it.label })
                    when (cocok.size) {
                        1 -> {
                            val app = daftar.first { it.label == cocok[0] }
                            if (peluncur.buka(app)) {
                                status = "Buka " + app.label
                                Riwayat.tambah("Buka " + app.label, "Berhasil")
                            } else {
                                status = "Aplikasi tidak ditemukan"
                            }
                        }
                        0 -> {
                            status = "Berpikir"
                            jawaban = ""
                            when (val hasil = klien.tanya(bersih)) {
                                is KlienModel.Hasil.Sukses -> {
                                    jawaban = hasil.jawaban
                                    status = "Jawaban Nazeio"
                                    Riwayat.tambah("Tanya " + bersih.take(24), "Berhasil")
                                    pembicara.bicara(Pembicara.ringkasUntukBaca(hasil.jawaban))
                                }
                                is KlienModel.Hasil.Gagal -> status = hasil.pesan
                            }
                        }
                        else -> status = "Ada beberapa yang mirip. Mana yang dimaksud"
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(Warna.Biru, CircleShape)
            )
            Text(
                text = "Nazeio",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = if (siaga) "Siaga" else "Mati",
                fontSize = 12.sp,
                color = if (siaga) Warna.Biru else Warna.TeksRedup,
                modifier = Modifier
                    .background(
                        if (siaga) Color(0xFFEAF1FF) else Warna.Kartu,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        Text(
            text = "Apa yang bisa saya bantu?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 28.dp)
        )
        Text(
            text = "Ucapkan Nazeio kapan saja.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Box(
            modifier = Modifier
                .padding(top = 36.dp)
                .size(132.dp)
                .border(
                    2.dp,
                    if (mendengar) Warna.Ungu else Warna.Biru,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .background(
                        if (mendengar) Warna.Ungu else Warna.Biru,
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
                Icon(
                    Icons.Filled.Mic,
                    contentDescription = "Mikrofon",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Text(
            text = if (mendengar) "Sedang mendengar" else "Atau ketuk untuk berbicara",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (mendengar) {
            GelombangSuara(warna = Warna.Ungu, modifier = Modifier.padding(top = 16.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Mode siaga", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        if (siaga) "Mendengar kata Nazeio" else "Tidak mendengarkan",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = siaga,
                    onCheckedChange = { nyala ->
                        mauSiaga = nyala
                        if (nyala) {
                            if (!adaIzin) {
                                izinLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    )
                                )
                            } else {
                                ControlLayanan.nyalakan(context)
                            }
                        } else {
                            ControlLayanan.matikan(context)
                        }
                    }
                )
            }
        }

        if (jawaban.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF2ECFF)
                )
            ) {
                Text(
                    text = jawaban,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Text(
            text = status,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )

        if (pembicara.sedangBicara) {
            TextButton(onClick = { pembicara.hentikan() }) { Text("Hentikan suara") }
        }

        if (Riwayat.entri.isNotEmpty()) {
            Text(
                text = "TERAKHIR",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
            )
            Riwayat.entri.take(4).forEach { e ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(e.first, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    Text(
                        e.second,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = e.third,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Bar gelombang lima batang sesuai desain layar Mendengar.
 */
@Composable
fun GelombangSuara(warna: Color, modifier: Modifier = Modifier) {
    val transisi = rememberInfiniteTransition(label = "gelombang")
    val fase by transisi.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "fase"
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        (0 until 5).forEach { i ->
            val tinggi = 0.25f + 0.75f * kotlin.math.abs(
                kotlin.math.sin((fase + i * 0.2f) * 3.14159f)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .width(6.dp)
                    .height((6 + 26 * tinggi).dp)
                    .background(warna, RoundedCornerShape(3.dp))
            )
        }
    }
}
