package com.nazeio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nazeio.app.ui.AliasScreen
import com.nazeio.app.ui.BerandaScreen
import com.nazeio.app.ui.NavigasiBawah
import com.nazeio.app.ui.NazeioTheme
import com.nazeio.app.ui.PengaturanScreen
import com.nazeio.app.ui.RiwayatScreen

/**
 * Aktivitas utama: empat tab bawah sesuai desain
 * (Beranda, Riwayat, Alias, Pengaturan).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NazeioTheme {
                var tab by remember { mutableIntStateOf(0) }
                Scaffold(
                    bottomBar = { NavigasiBawah(terpilih = tab, pilih = { tab = it }) }
                ) { dalam ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dalam)
                    ) {
                        when (tab) {
                            0 -> BerandaScreen()
                            1 -> RiwayatScreen()
                            2 -> AliasScreen()
                            else -> PengaturanScreen()
                        }
                    }
                }
            }
        }
    }
}
