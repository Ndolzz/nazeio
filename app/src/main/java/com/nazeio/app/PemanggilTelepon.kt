package com.nazeio.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract

data class Kontak(val nama: String, val nomor: String)

/**
 * Menelepon lewat dialer sesuai spesifikasi 08.
 * Hanya membuka dialer dengan nomor sudah terisi, tidak menelepon langsung,
 * sehingga pengguna selalu menekan tombol panggilan sendiri.
 */
class PemanggilTelepon(private val context: Context) {

    fun bukaDialer(nomor: String): Boolean {
        return try {
            val niat = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + nomor))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(niat)
            true
        } catch (e: Exception) {
            false
        }
    }

    /** True bila izin kontak sudah diberikan pengguna. */
    fun izinKontak(): Boolean =
        context.checkSelfPermission(Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    /** Mencari kontak yang namanya memuat teks, duplikat nomor dibuang. */
    fun cariKontak(nama: String): List<Kontak> {
        val hasil = mutableListOf<Kontak>()
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
            arrayOf("%" + nama + "%"),
            null
        )?.use { kursor ->
            while (kursor.moveToNext()) {
                hasil.add(Kontak(kursor.getString(0), kursor.getString(1)))
            }
        }
        return hasil.distinctBy { it.nomor }
    }
}
