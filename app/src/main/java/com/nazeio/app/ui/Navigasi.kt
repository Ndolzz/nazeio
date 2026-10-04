package com.nazeio.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class ItemNav(val ikon: ImageVector, val label: String)

val daftarNav = listOf(
    ItemNav(Icons.Filled.Home, "Beranda"),
    ItemNav(Icons.Filled.Schedule, "Riwayat"),
    ItemNav(Icons.Filled.Alarm, "Pengingat"),
    ItemNav(Icons.Filled.Label, "Alias"),
    ItemNav(Icons.Filled.Settings, "Pengaturan")
)

@Composable
fun NavigasiBawah(terpilih: Int, pilih: (Int) -> Unit) {
    NavigationBar {
        daftarNav.forEachIndexed { i, item ->
            NavigationBarItem(
                selected = terpilih == i,
                onClick = { pilih(i) },
                icon = { Icon(item.ikon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
