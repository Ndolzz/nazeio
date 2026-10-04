package com.nazeio.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object Warna {
    val Biru = Color(0xFF2563EB)
    val Ungu = Color(0xFF7C3AED)
    val Merah = Color(0xFFDC2626)
    val Teks = Color(0xFF0F172A)
    val TeksRedup = Color(0xFF64748B)
    val Kartu = Color(0xFFF8FAFC)
    val Garis = Color(0xFFE2E8F0)
    val Abu = Color(0xFF94A3B8)
}

@Composable
fun NazeioTheme(content: @Composable () -> Unit) {
    val gelap = isSystemInDarkTheme()
    val skema = if (gelap) {
        darkColorScheme(
            primary = Warna.Biru,
            secondary = Warna.Ungu,
            error = Warna.Merah,
            background = Color(0xFF0F172A),
            surface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFF162033)
        )
    } else {
        lightColorScheme(
            primary = Warna.Biru,
            secondary = Warna.Ungu,
            error = Warna.Merah,
            background = Color.White,
            surface = Color.White,
            surfaceVariant = Warna.Kartu
        )
    }
    MaterialTheme(colorScheme = skema, content = content)
}
