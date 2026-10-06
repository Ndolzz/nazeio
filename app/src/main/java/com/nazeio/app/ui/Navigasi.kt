package com.nazeio.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ItemNav(val ikon: ImageVector, val label: String)

val daftarNav = listOf(
    ItemNav(Icons.Filled.Home, "Beranda"),
    ItemNav(Icons.Filled.Schedule, "Riwayat"),
    ItemNav(Icons.Filled.Label, "Alias"),
    ItemNav(Icons.Filled.Settings, "Pengaturan")
)

@Composable
fun NavigasiBawah(terpilih: Int, pilih: (Int) -> Unit) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            daftarNav.forEachIndexed { i, item ->
                val dipilih = terpilih == i
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clickable { pilih(i) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        item.ikon,
                        contentDescription = item.label,
                        tint = if (dipilih) Warna.Biru else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                    Text(
                        item.label,
                        color = if (dipilih) Warna.Biru else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = if (dipilih) FontWeight.SemiBold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
