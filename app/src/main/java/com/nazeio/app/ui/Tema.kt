package com.nazeio.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
            surface = Color(0xFF111C2F),
            surfaceVariant = Color(0xFF1A2940)
        )
    } else {
        lightColorScheme(
            primary = Warna.Biru,
            secondary = Warna.Ungu,
            error = Warna.Merah,
            background = Color(0xFFF7F9FC),
            surface = Color.White,
            surfaceVariant = Color(0xFFF1F5FA)
        )
    }
    MaterialTheme(
        colorScheme = skema,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        content = content
    )
}
