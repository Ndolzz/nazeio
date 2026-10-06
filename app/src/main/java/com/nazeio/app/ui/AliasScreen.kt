package com.nazeio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nazeio.app.data.RepositoriAlias
import kotlinx.coroutines.launch

/**
 * Layar Alias sesuai desain: pencarian, kelompok aksi
 * dengan tag alias, dan tombol tambah alias.
 */
@Composable
fun AliasScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositori = remember { RepositoriAlias(context) }
    var daftar by remember { mutableStateOf(listOf<com.nazeio.app.data.AliasEntity>()) }
    var cari by remember { mutableStateOf("") }
    var bukaDialog by remember { mutableStateOf(false) }
    var aksiBaru by remember { mutableStateOf("") }
    var aliasBaru by remember { mutableStateOf("") }

    fun muat() {
        scope.launch {
            repositori.pastikanDataAwal()
            daftar = repositori.semua()
        }
    }

    remember { muat(); true }

    val kelompok = daftar
        .filter { it.alias.contains(cari, true) || it.aksi.contains(cari, true) }
        .groupBy { it.aksi }
        .map { (aksi, list) -> aksi to list.map { it.alias } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 22.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(10.dp).background(Warna.Ungu, CircleShape))
            Text(
                "Nazeio",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${daftar.size} alias",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Warna.Biru,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        JudulHalaman("Alias", "Satu aksi, banyak sebutan.")

        OutlinedTextField(
            value = cari,
            onValueChange = { cari = it },
            placeholder = { Text("Cari alias") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(kelompok.size) { i ->
                val (aksi, tag) = kelompok[i]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            aksi.replace("panel_", "").replace("_", " ")
                                .replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            tag.forEach { t ->
                                Text(
                                    t,
                                    fontSize = 12.sp,
                                    color = Warna.Biru,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { bukaDialog = true },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Warna.Biru),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) { Text("Tambah alias") }
    }

    if (bukaDialog) {
        AlertDialog(
            onDismissRequest = { bukaDialog = false },
            title = { Text("Tambah alias") },
            text = {
                Column {
                    OutlinedTextField(
                        value = aksiBaru,
                        onValueChange = { aksiBaru = it },
                        label = { Text("Nama aksi, contoh panel_bluetooth") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = aliasBaru,
                        onValueChange = { aliasBaru = it },
                        label = { Text("Alias baru") },
                        singleLine = true,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (aksiBaru.isNotBlank() && aliasBaru.isNotBlank()) {
                        scope.launch {
                            repositori.tambahAlias(aksiBaru.trim(), aliasBaru.trim())
                            aksiBaru = ""
                            aliasBaru = ""
                            bukaDialog = false
                            muat()
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { bukaDialog = false }) { Text("Batal") }
            }
        )
    }
}
