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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.nazeio.app.LayananSiaga
import com.nazeio.app.data.Pengaturan
import kotlinx.coroutines.launch

/**
 * Layar Pengaturan sesuai desain: bagian Tampilan dengan
 * pratinjau gaya animasi, Siaga, Suara, Izin, dan API.
 */
@Composable
fun PengaturanScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pengaturan = remember { Pengaturan(context) }

    var gaya by remember { mutableStateOf(pengaturan.gayaAnimasi) }
    var hemat by remember { mutableStateOf(pengaturan.hematBaterai) }
    var statusPratinjau by remember { mutableStateOf("aktif") }
    var penyedia by remember { mutableStateOf(pengaturan.penyediaApi) }
    var kunci by remember { mutableStateOf(pengaturan.kunciApi) }
    var batas by remember { mutableStateOf(pengaturan.batasHarian.toFloat()) }
    var siaga by remember { mutableStateOf(false) }
    var izinMikrofon by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val izinLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { hasil ->
        izinMikrofon = hasil[Manifest.permission.RECORD_AUDIO] == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Pengaturan", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(
            "Atur tampilan dan perilaku asisten.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        BagianJudul("TAMPILAN")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Gaya animasi", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(
                    "Berlaku untuk semua widget",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        PratinjauAnimasi(gaya = gaya, status = statusPratinjau, hemat = hemat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("mati", "siaga", "aktif").forEach { s ->
                Text(
                    s.replaceFirstChar { it.uppercase() },
                    fontSize = 12.sp,
                    color = if (statusPratinjau == s) Color.White else Warna.Teks,
                    modifier = Modifier
                        .background(
                            if (statusPratinjau == s) Warna.Biru else Warna.Kartu,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { statusPratinjau = s }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
        Text(
            "Pratinjau status",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "gelombang" to "Gelombang",
                "kipas" to "Kipas",
                "badai" to "Badai"
            ).forEach { (kode, nama) ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            gaya = kode
                            pengaturan.gayaAnimasi = kode
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (gaya == kode) Color(0xFFEAF1FF)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PratinjauMini(gaya = kode, status = statusPratinjau)
                        Text(
                            nama,
                            fontSize = 12.sp,
                            fontWeight = if (gaya == kode) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (gaya == kode) Warna.Biru else Warna.Teks,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        BarisSakelar(
            judul = "Hemat baterai",
            keterangan = "Kurangi bingkai dan matikan neon berputar",
            nyala = hemat,
            ubah = {
                hemat = it
                pengaturan.hematBaterai = it
            }
        )

        BagianJudul("SIAGA")
        BarisSakelar(
            judul = "Mode siaga",
            keterangan = "Mendengar kata pemicu",
            nyala = siaga,
            ubah = { nyala ->
                siaga = nyala
                if (nyala) {
                    if (!izinMikrofon) {
                        izinLauncher.launch(
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        )
                    } else {
                        context.startForegroundService(
                            Intent(context, LayananSiaga::class.java)
                        )
                    }
                } else {
                    context.stopService(Intent(context, LayananSiaga::class.java))
                }
            }
        )
        BarisNilai("Kata pemicu", "Nazeio")
        BarisNilai("Batas diam", "8 detik")

        BagianJudul("SUARA")
        BarisNilai("Suara bicara", "Indonesia, natural")
        BarisNilai("Kecepatan bicara", "Normal")

        BagianJudul("IZIN")
        BarisChip("Mikrofon", if (izinMikrofon) "Aktif" else "Perlu izin", izinMikrofon)
        BarisChip("Aksesibilitas", "Perlu izin", false)
        BarisChip("Tampil di atas aplikasi lain", "Perlu izin", false)

        BagianJudul("AI")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("gemini" to "Gemini", "claude" to "Claude").forEach { (kode, nama) ->
                Text(
                    if (penyedia == kode) nama + " (aktif)" else nama,
                    fontSize = 13.sp,
                    color = if (penyedia == kode) Warna.Biru else Warna.TeksRedup,
                    modifier = Modifier
                        .background(Warna.Kartu, RoundedCornerShape(20.dp))
                        .clickable { penyedia = kode }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
        OutlinedTextField(
            value = kunci,
            onValueChange = { kunci = it },
            label = { Text("Kunci API") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        Text(
            "Batas pemakaian harian: " + batas.toInt() + " permintaan",
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 14.dp)
        )
        Slider(
            value = batas,
            onValueChange = { batas = it },
            valueRange = 10f..200f,
            steps = 18
        )
        Text(
            "Pemakaian hari ini: " + pengaturan.pemakaianHari + " dari " + pengaturan.batasHarian,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(
            onClick = {
                scope.launch {
                    pengaturan.penyediaApi = penyedia
                    pengaturan.kunciApi = kunci.trim()
                    pengaturan.batasHarian = batas.toInt()
                }
            },
            modifier = Modifier.padding(bottom = 24.dp)
        ) { Text("Simpan") }
    }
}

@Composable
private fun BagianJudul(teks: String) {
    Text(
        teks,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
    )
}

@Composable
private fun BarisSakelar(judul: String, keterangan: String, nyala: Boolean, ubah: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(judul, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                keterangan,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = nyala, onCheckedChange = ubah)
    }
}

@Composable
private fun BarisNilai(judul: String, nilai: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(judul, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(nilai, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BarisChip(judul: String, nilai: String, ok: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(judul, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(
            nilai,
            fontSize = 12.sp,
            color = if (ok) Warna.Biru else Warna.Merah,
            modifier = Modifier
                .background(
                    if (ok) Color(0xFFEAF1FF) else Color(0xFFFDECEC),
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun warnaStatus(status: String): Color = when (status) {
    "aktif" -> Warna.Ungu
    "siaga" -> Warna.Biru
    else -> Warna.Abu
}

/**
 * Pratinjau gaya animasi sesuai spesifikasi 06:
 * Gelombang, Kipas, dan Badai dengan kecepatan mengikuti status.
 */
@Composable
fun PratinjauAnimasi(gaya: String, status: String, hemat: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, warnaStatus(status), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            BadanAnimasi(gaya, status, hemat, ukuran = 84)
        }
        Text(
            status.replaceFirstChar { it.uppercase() },
            fontSize = 13.sp,
            color = warnaStatus(status),
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun PratinjauMini(gaya: String, status: String) {
    BadanAnimasi(gaya, status, hemat = false, ukuran = 40)
}

@Composable
private fun BadanAnimasi(gaya: String, status: String, hemat: Boolean, ukuran: Int) {
    val warna = warnaStatus(status)
    val kecepatan = if (status == "aktif") 600 else if (status == "siaga") 1400 else 2400
    val transisi = rememberInfiniteTransition(label = "anim")
    val fase by transisi.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(kecepatan), RepeatMode.Reverse),
        label = "fase"
    )
    val diameter = ukuran.dp
    Box(modifier = Modifier.size(diameter), contentAlignment = Alignment.Center) {
        when (gaya) {
            "kipas" -> {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(diameter)
                        .rotate(if (hemat) 0f else fase * 120f)
                ) {
                    val lebar = size.width
                    repeat(3) { i ->
                        rotate(i * 120f) {
                            drawOval(
                                color = warna,
                                size = androidx.compose.ui.geometry.Size(lebar * 0.18f, lebar * 0.38f),
                                topLeft = androidx.compose.ui.geometry.Offset(
                                    lebar * 0.41f, lebar * 0.08f
                                )
                            )
                        }
                    }
                    drawCircle(Color.White, radius = lebar * 0.08f)
                }
            }
            "badai" -> {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(diameter)) {
                    val lebar = size.width
                    drawCircle(warna, radius = lebar * 0.08f)
                    if (!hemat) {
                        rotate(fase * 360f) {
                            drawCircle(
                                warna,
                                radius = lebar * 0.4f,
                                style = Stroke(width = lebar * 0.05f)
                            )
                        }
                        rotate(-fase * 360f) {
                            drawCircle(
                                warna,
                                radius = lebar * 0.24f,
                                style = Stroke(width = lebar * 0.05f)
                            )
                        }
                    } else {
                        drawCircle(
                            warna,
                            radius = lebar * 0.4f,
                            style = Stroke(width = lebar * 0.05f)
                        )
                        drawCircle(
                            warna,
                            radius = lebar * 0.24f,
                            style = Stroke(width = lebar * 0.05f)
                        )
                    }
                }
            }
            else -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { i ->
                        val tinggi =
                            if (status == "mati") 0.25f
                            else 0.25f + 0.75f * kotlin.math.abs(
                                kotlin.math.sin((fase + i * 0.2f) * 3.14159f)
                            )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .height((ukuran * 0.6 * tinggi).dp)
                                .background(warna, RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }
    }
}
