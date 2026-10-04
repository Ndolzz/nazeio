package com.nazeio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazeio.app.data.Pengaturan
import kotlinx.coroutines.launch

/**
 * Layar Pengaturan sesuai spesifikasi 03:
 * pilih penyedia API, kunci API, dan batas pemakaian harian.
 */
@Composable
fun PengaturanScreen(kembali: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pengaturan = remember { Pengaturan(context) }

    var penyedia by remember { mutableStateOf(pengaturan.penyediaApi) }
    var kunci by remember { mutableStateOf(pengaturan.kunciApi) }
    var batas by remember { mutableStateOf(pengaturan.batasHarian.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pengaturan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = kembali) { Text("Kembali") }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Penyedia API", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = { penyedia = "gemini" }) {
                        Text(
                            if (penyedia == "gemini") "Gemini (aktif)" else "Gemini",
                            color = if (penyedia == "gemini") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { penyedia = "claude" }) {
                        Text(
                            if (penyedia == "claude") "Claude (aktif)" else "Claude",
                            color = if (penyedia == "claude") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = kunci,
                    onValueChange = { kunci = it },
                    label = { Text("Kunci API") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    singleLine = true
                )

                Text(
                    "Batas pemakaian harian: " + batas.toInt() + " permintaan",
                    modifier = Modifier.padding(top = 20.dp)
                )
                Slider(
                    value = batas,
                    onValueChange = { batas = it },
                    valueRange = 10f..200f,
                    steps = 18
                )

                TextButton(
                    onClick = {
                        scope.launch {
                            pengaturan.penyediaApi = penyedia
                            pengaturan.kunciApi = kunci.trim()
                            pengaturan.batasHarian = batas.toInt()
                        }
                        kembali()
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) { Text("Simpan") }
            }
        }

        Text(
            text = "Kunci API disimpan terenkripsi di perangkat. Pemakaian hari ini: " +
                pengaturan.pemakaianHari + " dari " + pengaturan.batasHarian + ".",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
