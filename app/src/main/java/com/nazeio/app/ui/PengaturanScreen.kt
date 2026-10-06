package com.nazeio.app.ui

import android.Manifest
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate as putar
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nazeio.app.ControlLayanan
import com.nazeio.app.data.Pengaturan
import com.nazeio.app.widget.PembaruWidget
import com.nazeio.app.widget.StatusBersama
import kotlin.math.abs
import kotlin.math.sin

/**
 * Layar Pengaturan sesuai desain: bagian Tampilan dengan
 * pratinjau gaya animasi, Siaga, Suara, Izin, dan API.
 *
 * Catatan: drawscope.rotate dipakai di dalam Canvas,
 * sedangkan putar (Modifier.rotate) memutar badan kipas.
 */
@Composable
fun PengaturanScreen(bukaPengingat: () -> Unit = {}) {
    val context = LocalContext.current
    val pengaturan = remember { Pengaturan(context) }

    var gaya by remember { mutableStateOf(pengaturan.gayaAnimasi) }
    var hemat by remember { mutableStateOf(pengaturan.hematBaterai) }
    var penyedia by remember { mutableStateOf(pengaturan.penyediaApi) }
    var kunci by remember { mutableStateOf(pengaturan.kunciApi) }
    var batas by remember { mutableStateOf(pengaturan.batasHarian.toFloat()) }
    var statusPratinjau by remember { mutableStateOf("aktif") }
    var apiTerbuka by remember { mutableStateOf(false) }
    val siaga = StatusBersama.status != StatusBersama.Status.MATI

    var izinMikrofon by remember { mutableStateOf(adaIzinMikrofon(context)) }
    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { izinMikrofon = adaIzinMikrofon(context) }

    fun nyalakanSiaga(nyala: Boolean) {
        if (nyala) {
            if (izinMikrofon) {
                ControlLayanan.nyalakan(context)
            } else {
                izinLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }
        } else {
            ControlLayanan.matikan(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        JudulHalaman("Pengaturan", "Atur tampilan dan perilaku asisten.")
        BagianJudul("TAMPILAN")
        Text("Gaya animasi", fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(
            "Berlaku untuk semua widget",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp))
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(156.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OrbPratinjau(gaya, hemat, statusPratinjau)
                    Text("Nazeio", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        statusPratinjau.replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        color = when (statusPratinjau) {
                            "aktif" -> Warna.Ungu
                            "siaga" -> Warna.Biru
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf("mati" to "Mati", "siaga" to "Siaga", "aktif" to "Aktif").forEach { (status, label) ->
                    Text(
                        label,
                        color = if (statusPratinjau == status) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (statusPratinjau == status) Warna.Biru else Color.Transparent)
                            .clickable { statusPratinjau = status }
                            .padding(horizontal = 13.dp, vertical = 7.dp)
                    )
                }
            }
            Text(
                "Pratinjau status",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
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
                        PembaruWidget.penuh(context)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        BarisSakelar(
            judul = "Hemat baterai",
            sub = "Kurangi bingkai dan matikan neon berputar",
            nilai = hemat,
            ubah = {
                hemat = it
                pengaturan.hematBaterai = it
                PembaruWidget.penuh(context)
            }
        )

        BagianJudul("SIAGA")
        BarisSakelar(
            judul = "Mode siaga",
            sub = "Mendengar kata pemicu",
            nilai = siaga,
            ubah = { nyalakanSiaga(it) }
        )
        BarisNilai("Kata pemicu", "Nazeio")
        BarisNilai("Batas diam", "8 detik", "Percakapan berakhir otomatis")
        BarisAksi("Pengingat", "Lihat dan kelola jadwal.", bukaPengingat)

        BagianJudul("SUARA")
        BarisNilai("Suara bicara", "Bahasa Indonesia")
        BarisNilai("Kecepatan bicara", "Normal")

        BagianJudul("IZIN")
        BarisChip("Mikrofon", izinMikrofon)
        if (!izinMikrofon) {
            TextButton(onClick = {
                izinLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
            }) { Text("Berikan izin") }
        }

        TextButton(
            onClick = { apiTerbuka = !apiTerbuka },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) {
            Text(if (apiTerbuka) "Tutup pengaturan API" else "Pengaturan API kecerdasan")
        }
        if (apiTerbuka) {
            OutlinedTextField(
                value = penyedia,
                onValueChange = {
                    penyedia = it
                    pengaturan.penyediaApi = it
                },
                label = { Text("Penyedia (gemini / claude / gpt)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
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
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Text(
                "Batas permintaan harian: ${batas.toInt()}",
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
        }
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
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(judul, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(sub, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SakelarPengaturan(nilai, judul) { ubah(!nilai) }
        }
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun BarisNilai(judul: String, nilai: String, sub: String? = null) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(judul, fontSize = 14.sp)
                if (sub != null) {
                    Text(sub, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(nilai, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun BarisAksi(judul: String, sub: String, saatKlik: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = saatKlik).padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(judul, fontSize = 14.sp)
                Text(sub, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Buka $judul",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun BarisChip(judul: String, aktif: Boolean) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(judul, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(
                if (aktif) "Aktif" else "Perlu izin",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (aktif) Warna.Biru else Warna.Merah,
                modifier = Modifier
                    .background(
                        if (aktif) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.errorContainer,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
            .background(
                if (dipilih) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                if (dipilih) 1.5.dp else 1.dp,
                if (dipilih) Warna.Biru else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(16.dp)
            )
            .clickable { saatKlik() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OrbPratinjau(nama, hemat, "aktif", ukuran = 44.dp)
        Text(
            nama.replaceFirstChar { it.uppercase() },
            fontSize = 12.sp,
            fontWeight = if (dipilih) FontWeight.Bold else FontWeight.Normal,
            color = if (dipilih) Warna.Biru else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (dipilih) {
            Text(
                "Dipilih",
                fontSize = 10.sp,
                color = Warna.Biru,
                modifier = Modifier.padding(top = 2.dp),
                maxLines = 1
            )
        } else {
            Spacer(Modifier.height(12.dp).padding(top = 2.dp))
        }
    }
}

@Composable
private fun OrbPratinjau(gaya: String, hemat: Boolean, status: String, ukuran: androidx.compose.ui.unit.Dp = 52.dp) {
    Box(
        modifier = Modifier
            .size(ukuran)
            .clip(RoundedCornerShape(50))
            .background(
                if (status == "mati") SolidColor(MaterialTheme.colorScheme.outlineVariant)
                else Brush.linearGradient(
                    if (status == "aktif") listOf(Warna.Ungu, Warna.Biru)
                    else listOf(Warna.Biru, Warna.Ungu)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (status == "mati") {
            Icon(
                Icons.Filled.Mic,
                contentDescription = null,
                tint = Warna.TeksRedup,
                modifier = Modifier.size(22.dp)
            )
        } else {
            BadanAnimasi(
                gaya = gaya,
                hemat = hemat,
                modifier = Modifier.size(ukuran * 0.52f),
                warna = Color.White
            )
        }
    }
}

@Composable
private fun SakelarPengaturan(aktif: Boolean, label: String, saatKlik: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 26.dp)
            .clip(RoundedCornerShape(50))
            .background(if (aktif) Warna.Biru else MaterialTheme.colorScheme.outlineVariant)
            .semantics {
                contentDescription = label
                role = Role.Switch
            }
            .toggleable(value = aktif, role = Role.Switch, onValueChange = { saatKlik() })
    ) {
        Box(
            modifier = Modifier
                .align(if (aktif) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 3.dp)
                .size(20.dp)
                .background(Color.White, RoundedCornerShape(50))
        )
    }
}

/**
 * Badan animasi sesuai gaya: gelombang batang, kipas berputar,
 * atau lingkaran badai. rotate di dalam Canvas berasal dari
 * drawscope, sedangkan putar (Modifier.rotate) memutar kipas.
 */
@Composable
private fun BadanAnimasi(
    gaya: String,
    hemat: Boolean,
    modifier: Modifier = Modifier,
    warna: Color = Warna.Biru
) {
    val transisi = rememberInfiniteTransition(label = "pratinjau")
    val fase by transisi.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (hemat) 1500 else 600), RepeatMode.Restart),
        label = "fase"
    )

    when (gaya) {
        "kipas" -> {
            Canvas(
                modifier = modifier
                    .size(48.dp)
                    .putar(if (hemat) fase * 60f else fase * 360f)
            ) {
                val lebar = size.minDimension
                repeat(3) { i ->
                    rotate(i * 120f) {
                        drawOval(
                            color = warna,
                            topLeft = Offset(lebar * 0.42f, lebar * 0.06f),
                            size = Size(lebar * 0.16f, lebar * 0.44f)
                        )
                    }
                }
                drawCircle(color = Warna.Ungu, radius = lebar * 0.08f)
            }
        }
        "badai" -> {
            Canvas(modifier = modifier.size(48.dp)) {
                val lebar = size.minDimension
                rotate(if (hemat) fase * 120f else fase * 360f) {
                    drawCircle(
                        color = warna,
                        radius = lebar * 0.4f,
                        style = Stroke(width = lebar * 0.06f)
                    )
                }
                drawCircle(
                    color = warna,
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
                            .background(warna, RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}
