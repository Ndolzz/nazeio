package com.nazeio.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log

data class AplikasiInfo(
    val label: String,
    val packageName: String,
    val intentPeluncur: Intent
)

/**
 * Membaca daftar aplikasi terpasang dari PackageManager,
 * menampilkan nama aplikasi yang bisa dibuka pengguna.
 */
class PeluncurAplikasi(private val context: Context) {

    fun daftarAplikasi(): List<AplikasiInfo> {
        val pm = context.packageManager
        val niat = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(niat, 0)
            .map { info ->
                AplikasiInfo(
                    label = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    intentPeluncur = pm.getLaunchIntentForPackage(info.activityInfo.packageName)
                        ?: Intent(Intent.ACTION_MAIN).apply {
                            setClassName(info.activityInfo.packageName, info.activityInfo.name)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                )
            }
            .filter { it.packageName != context.packageName }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun buka(aplikasi: AplikasiInfo): Boolean {
        return try {
            context.startActivity(
                aplikasi.intentPeluncur.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Aktivitas peluncur tidak ditemukan: ${aplikasi.packageName}", e)
            false
        } catch (e: SecurityException) {
            Log.e(TAG, "Tidak diizinkan membuka aplikasi: ${aplikasi.packageName}", e)
            false
        }
    }

    companion object {
        private const val TAG = "PeluncurAplikasi"
    }
}
