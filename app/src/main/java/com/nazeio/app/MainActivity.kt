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
import com.nazeio.app.ui.PengingatScreen
import com.nazeio.app.ui.RiwayatScreen
import com.nazeio.app.widget.StatusBersama

/**
 * Aktivitas utama: lima tab bawah sesuai desain
 * (Beranda, Riwayat, Pengingat, Alias, Pengaturan).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBersama.muat(this)
        Riwayat.muat(this)
        // Izin kontak untuk fitur telepon, diminta sekali di awal.
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 1)
        }
        // Izin Bluetooth untuk fitur kontrol Bluetooth di Android 12 ke atas.
        if (android.os.Build.VERSION.SDK_INT >= 31 &&
            checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT), 2)
        }
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
                            2 -> PengingatScreen()
                            3 -> AliasScreen()
                            else -> PengaturanScreen()
                        }
                    }
                }
            }
        }
    }
}
