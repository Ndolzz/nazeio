package com.nazeio.app

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Mengendalikan Bluetooth sesuai spesifikasi 13.
 * Menyalakan dan mematikan butuh izin BLUETOOTH_CONNECT di Android 12
 * ke atas, sedangkan di Android 11 berjalan tanpa izin tambahan.
 */
class PengelolaBluetooth(private val context: Context) {

    private val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter

    /** True bila izin Bluetooth yang dibutuhkan versi Android ini sudah ada. */
    fun izin(): Boolean {
        if (Build.VERSION.SDK_INT < 31) return true
        return context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
            PackageManager.PERMISSION_GRANTED
    }

    fun nyalakan(): Boolean {
        val a = adapter ?: return false
        return try {
            a.isEnabled || a.enable()
        } catch (e: SecurityException) {
            false
        }
    }

    fun matikan(): Boolean {
        val a = adapter ?: return false
        return try {
            !a.isEnabled || a.disable()
        } catch (e: SecurityException) {
            false
        }
    }

    fun status(): String {
        val a = adapter ?: return "Bluetooth tidak tersedia di perangkat ini"
        return try {
            if (a.isEnabled) "Bluetooth menyala" else "Bluetooth mati"
        } catch (e: SecurityException) {
            "Status Bluetooth tidak bisa dibaca"
        }
    }
}
