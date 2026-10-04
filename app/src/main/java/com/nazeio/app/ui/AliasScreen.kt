package com.nazeio.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
 * Layar Alias sesuai spesifikasi 02:
 * melihat, menambah, dan menghapus alias.
 */
@Composable
fun AliasScreen(kembali: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repositori = remember { RepositoriAlias(context) }
    var daftar by remember { mutableStateOf(listOf<com.nazeio.app.data.AliasEntity>()) }
    var aliasBaru by remember { mutableStateOf("") }
    var aksiBaru by remember { mutableStateOf("") }

    fun muat() {
        scope.launch {
            repositori.pastikanDataAwal()
            daftar = repositori.semua()
        }
    }

    remember { muat(); true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alias",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = kembali) { Text("Kembali") }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = aksiBaru,
                onValueChange = { aksiBaru = it },
                label = { Text("Aksi") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = aliasBaru,
                onValueChange = { aliasBaru = it },
                label = { Text("Alias baru") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        TextButton(
            onClick = {
                if (aksiBaru.isNotBlank() && aliasBaru.isNotBlank()) {
                    scope.launch {
                        repositori.tambahAlias(aksiBaru.trim(), aliasBaru.trim())
                        aliasBaru = ""
                        muat()
                    }
                }
            },
            modifier = Modifier.padding(top = 4.dp)
        ) { Text("Tambah alias") }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(daftar, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = item.alias, fontSize = 15.sp)
                            Text(
                                text = item.aksi,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = {
                            scope.launch {
                                repositori.hapus(item.id)
                                muat()
                            }
                        }) { Text("Hapus") }
                    }
                }
            }
        }
    }
}
