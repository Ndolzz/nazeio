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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nazeio.app.LayananSiaga
import com.nazeio.app.data.Pengaturan
import kotlin.math.abs
import kotlin.math.sin

/**
 * Layar Pengaturan sesuai desain: bagian Tampilan dengan
 * pratinjau gaya animasi, Siaga, Suara, Izin, dan API.
 */
@Composable
fun PengaturanScreen() {
    val context = LocalContext.current
    val pengaturan = remember { Pengaturan(context) }

    var gaya by remember { mutableStateOf(pengaturan.gayaAnimasi) }
    var hemat by remember { mutableStateOf(pengaturan.hematBaterai) }
    var penyedia by remember { mutableStateOf(pengaturan.penyediaApi) }
    var kunci by remember { mutableStateOf(pengaturan.kunciApi) }
    var batas by remember { mutableStateOf(pengaturan.batasHarian.toFloat()) }
    var siaga by remember { mutableStateOf(false) }

    var izinMikrofon by remember { mutableStateOf(adaIzinMikrofon(context)) }
    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { izinMikrofon = adaIzinMikrofon(context) }

    fun nyalakanSiaga(nyala: Boolean) {
        siaga = nyala
        if (nyala) {
            if (izinMikrofon) {
                context.startForegroundService(Intent(context, LayananSiaga::class.java))
            } else {
                siaga = false
                izinLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }
        } else {
            context.stopService(Intent(context, LayananSiaga::class.java))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Pengaturan", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "Sesuaikan Nazeio sesukamu.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // ===== TAMPILAN =====
        BagianJudul("TAMPILAN")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("gelombang", "kipas", "badai").forEach { nama ->
                PratinjauAnimasi(
                    nama = nama,
                    dipilih = gaya == nama,
                    hemat = hemat,
                    saatKlik = {
                        gaya = nama
                        pengaturan.gayaAnimasi = nama
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        BarisSakelar(
            judul = "Hemat baterai",
            sub = "Animasi diperlambat saat layar mati.",
            nilai = hemat,
            ubah = {
                hemat = it
                pengaturan.hematBaterai = it
            }
        )

        // ===== SIAGA =====
        BagianJudul("SIAGA")
        BarisSakelar(
            judul = "Mode siaga",
            sub = "Mendengar kata pemicu di latar belakang.",
            nilai = siaga,
            ubah = { nyalakanSiaga(it) }
        )
        BarisNilai("Kata pemicu", "\"Nazeio\"")
        BarisNilai("Batas diam", "8 detik")

        // ===== SUARA =====
        BagianJudul("SUARA")
        BarisNilai("Jawaban dibacakan", "Aktif")
        Text(
            "Nazeio membalas dengan suara bawaan perangkat.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // ===== IZIN =====
        BagianJudul("IZIN")
        BarisChip("Mikrofon", izinMikrofon)
        if (!izinMikrofon) {
            TextButton(onClick = {
                izinLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }) { Text("Berikan izin") }
        }

        // ===== API =====
        BagianJudul("API KECERDASAN")
        OutlinedTextField(
            value = penyedia,
            onValueChange = {
                penyedia = it
                pengaturan.penyediaApi = it
            },
            label = { Text("Penyedia (gemini / claude / gpt)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        OutlinedTextField(
            value = kunci,
            onValueChange = {
                kunci = it
                pengaturan.kunciApi = it
            },
            label = { Text("Kunci API") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Text(
            "Batas permintaan harian: " + batas.toInt(),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Slider(
            value = batas,
            onValueChange = {
                batas = it
                pengaturan.batasHarian = it.toInt()
            },
            valueRange = 10f..200f,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
    }
}

private fun adaIzinMikrofon(context: android.content.Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED

@Composable
private fun BagianJudul(teks: String) {
    Text(
        text = teks,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun BarisSakelar(judul: String, sub: String, nilai: Boolean, ubah: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(judul, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(
                sub,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = nilai, onCheckedChange = ubah)
    }
}

@Composable
private fun BarisNilai(judul: String, nilai: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(judul, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(nilai, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BarisChip(judul: String, aktif: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(judul, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(
            if (aktif) "Aktif" else "Perlu izin",
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier
                .background(
                    if (aktif) Warna.Biru else Warna.Merah,
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/**
 * Kartu pratinjau gaya animasi: Gelombang, Kipas, Badai.
 */
@Composable
private fun PratinjauAnimasi(
    nama: String,
    dipilih: Boolean,
    hemat: Boolean,
    saatKlik: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (dipilih) Modifier.border(2.dp, Warna.Biru, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { saatKlik() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(width = 64.dp, height = 52.dp), contentAlignment = Alignment.Center) {
            BadanAnimasi(gaya = nama, hemat = hemat)
        }
        Text(
            nama.replaceFirstChar { it.uppercase() },
            fontSize = 12.sp,
            fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal,
            color = if (dipilih) Warna.Biru else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Badan animasi sesuai gaya: gelombang batang, kipas berputar,
 * atau lingkaran badai.
 */
@Composable
private fun BadanAnimasi(gaya: String, hemat: Boolean, modifier: Modifier = Modifier) {
    val transisi = rememberInfiniteTransition(label = "pratinjau")
    val fase by transisi.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (hemat) 1500 else 600), RepeatMode.Restart),
        label = "fase"
    )

    when (gaya) {
        "kipas" -> {
            androidx.compose.ui.draw.rotate(
                degrees = if (hemat) fase * 60f else fase * 360f
            ) {
                Canvas(modifier = modifier.size(48.dp)) {
                    val lebar = size.minDimension
                    repeat(3) { i ->
                        rotate(i * 120f) {
                            drawOval(
                                color = Warna.Biru,
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    lebar * 0.42f,
                                    lebar * 0.06f
                                ),
                                size = androidx.compose.ui.geometry.Size(
                                    lebar * 0.16f,
                                    lebar * 0.44f
                                )
                            )
                        }
                    }
                    drawCircle(
                        color = Warna.Ungu,
                        radius = lebar * 0.08f
                    )
                }
            }
        }
        "badai" -> {
            Canvas(modifier = modifier.size(48.dp)) {
                val lebar = size.minDimension
                rotate(if (hemat) fase * 120f else fase * 360f) {
                    drawCircle(
                        color = Warna.Ungu,
                        radius = lebar * 0.4f,
                        style = Stroke(width = lebar * 0.06f)
                    )
                }
                drawCircle(
                    color = Warna.Biru,
                    radius = lebar * 0.24f,
                    style = Stroke(width = lebar * 0.05f)
                )
            }
        }
        else -> {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(5) { i ->
                    val tinggi = 0.25f + 0.75f * abs(sin((fase + i * 0.2f) * 3.14159f))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(width = 5.dp, height = (8 + 26 * tinggi).dp)
                            .background(Warna.Biru, RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}
