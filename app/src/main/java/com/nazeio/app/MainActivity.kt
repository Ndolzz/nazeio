package com.nazeio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import com.nazeio.app.ui.AliasScreen
import com.nazeio.app.ui.BerandaScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NazeioTheme {
                var layar by remember { mutableStateOf("beranda") }
                when (layar) {
                    "beranda" -> BerandaScreen(bukaLayarAlias = { layar = "alias" })
                    "alias" -> AliasScreen(kembali = { layar = "beranda" })
                }
            }
        }
    }
}

@Composable
fun NazeioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF2563EB),
            secondary = Color(0xFF7C3AED),
            error = Color(0xFFDC2626)
        ),
        content = content
    )
}
