package com.nazeio.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        daftarNav.forEachIndexed { i, item ->
            val dipilih = terpilih == i
            NavigationBarItem(
                selected = dipilih,
                onClick = { pilih(i) },
                icon = {
                    Icon(
                        item.ikon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        fontWeight = if (dipilih) FontWeight.SemiBold else FontWeight.Medium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Warna.Biru,
                    selectedTextColor = Warna.Biru,
                    indicatorColor = Warna.Biru.copy(
                        alpha = if (isSystemInDarkTheme()) 0.24f else 0.10f
                    ),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
