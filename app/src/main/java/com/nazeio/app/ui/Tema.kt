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
    val BiruLembut = Color(0xFFEAF1FF)
    val UnguLembut = Color(0xFFF2ECFF)
    val MerahLembut = Color(0xFFFDECEC)
}

@Composable
fun NazeioTheme(content: @Composable () -> Unit) {
    val gelap = isSystemInDarkTheme()
    val skema = if (gelap) {
        darkColorScheme(
            primary = Warna.Biru,
            secondary = Warna.Ungu,
            error = Warna.Merah,
            background = Color(0xFF0A0F1E),
            surface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFF162033),
            outlineVariant = Color(0xFF243049),
            primaryContainer = Color(0xFF16284F),
            secondaryContainer = Color(0xFF2A1F4D),
            errorContainer = Color(0xFF3A1A1F)
        )
    } else {
        lightColorScheme(
            primary = Warna.Biru,
            secondary = Warna.Ungu,
            error = Warna.Merah,
            background = Color.White,
            surface = Color.White,
            surfaceVariant = Warna.Kartu,
            outlineVariant = Warna.Garis,
            primaryContainer = Warna.BiruLembut,
            secondaryContainer = Warna.UnguLembut,
            errorContainer = Warna.MerahLembut
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
