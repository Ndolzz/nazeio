package com.nazeio.app.widget

import android.Manifest
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.nazeio.app.LayananSiaga
import com.nazeio.app.MainActivity
import com.nazeio.app.R
import com.nazeio.app.widget.StatusBersama.Status

/**
 * Membangun dan memperbarui semua widget Nazeio.
 * penuh: tampilan lengkap saat status berubah.
 * bingkai: hanya gambar animasi, dipanggil beberapa kali per detik.
 */
object PembaruWidget {

    private const val FLAG = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

    private enum class Jenis(
        val layout: Int,
        val kelas: Class<*>,
        val lebar: Int,
        val tinggi: Int,
        val radius: Float
    ) {
        KECIL(R.layout.widget_kecil, WidgetKecil::class.java, 240, 240, 0.18f),
        PANJANG(R.layout.widget_panjang, WidgetPanjang::class.java, 480, 96, 0.30f),
        BESAR(R.layout.widget_besar, WidgetBesar::class.java, 480, 220, 0.13f)
    }

    fun penuh(context: Context) {
        val app = context.applicationContext
        StatusBersama.muat(app)
        val am = AppWidgetManager.getInstance(app)
        for (j in Jenis.values()) {
            val ids = am.getAppWidgetIds(ComponentName(app, j.kelas))
            if (ids.isEmpty()) continue
            am.updateAppWidget(ids, bangun(app, j))
        }
    }

    fun bingkai(context: Context, nomor: Int) {
        val status = StatusBersama.status
        if (status == Status.MATI) return
        val app = context.applicationContext
        val am = AppWidgetManager.getInstance(app)
        for (j in Jenis.values()) {
            val ids = am.getAppWidgetIds(ComponentName(app, j.kelas))
            if (ids.isEmpty()) continue
            val rv = RemoteViews(app.packageName, j.layout)
            isiGambar(app, rv, j, status, nomor)
            am.partiallyUpdateAppWidget(ids, rv)
        }
    }

    /** Menampilkan teks ucapan terakhir di widget besar saat percakapan. */
    fun ucapan(context: Context, teks: String) {
        val app = context.applicationContext
        val am = AppWidgetManager.getInstance(app)
        val ids = am.getAppWidgetIds(ComponentName(app, WidgetBesar::class.java))
        if (ids.isEmpty()) return
        val rv = RemoteViews(app.packageName, R.layout.widget_besar)
        rv.setTextViewText(R.id.teks_ucapan, teks.take(60))
        am.partiallyUpdateAppWidget(ids, rv)
    }

    private fun bangun(context: Context, j: Jenis): RemoteViews {
        val status = StatusBersama.status
        val rv = RemoteViews(context.packageName, j.layout)
        rv.setTextViewText(R.id.teks_status, label(status))
        rv.setTextColor(R.id.teks_status, warna(context, status))
        rv.setImageViewResource(
            R.id.tombol_siaga,
            if (status == Status.MATI) R.drawable.tg_off else R.drawable.tg_on
        )
        rv.setViewVisibility(R.id.neon, if (status == Status.MATI) View.GONE else View.VISIBLE)
        rv.setOnClickPendingIntent(R.id.tombol_siaga, intentToggle(context, status))
        rv.setOnClickPendingIntent(R.id.orb, intentOrb(context, status))
        rv.setOnClickPendingIntent(R.id.teks_nama, intentApp(context))
        isiGambar(context, rv, j, status, 0)
        if (j == Jenis.BESAR) isiBody(context, rv, status)
        return rv
    }

    private fun isiGambar(context: Context, rv: RemoteViews, j: Jenis, status: Status, nomor: Int) {
        val gaya = PrefTampilan.gaya(context)
        val hemat = PrefTampilan.hemat(context)
        rv.setImageViewBitmap(R.id.orb, GambarWidget.orb(gaya, status, nomor))
        if (status != Status.MATI) {
            rv.setImageViewBitmap(
                R.id.neon,
                GambarWidget.neon(status, nomor, j.lebar, j.tinggi, j.radius, hemat)
            )
        }
    }

    private fun isiBody(context: Context, rv: RemoteViews, status: Status) {
        rv.setViewVisibility(R.id.body_mati, if (status == Status.MATI) View.VISIBLE else View.GONE)
        rv.setViewVisibility(R.id.body_siaga, if (status == Status.SIAGA) View.VISIBLE else View.GONE)
        rv.setViewVisibility(R.id.body_aktif, if (status == Status.AKTIF) View.VISIBLE else View.GONE)
        rv.setOnClickPendingIntent(R.id.tombol_nyalakan, intentToggle(context, status))
        rv.setOnClickPendingIntent(R.id.chip_aplikasi, intentApp(context))
        rv.setOnClickPendingIntent(
            R.id.chip_bluetooth,
            PendingIntent.getActivity(
                context, 20,
                Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                FLAG
            )
        )
        val wa = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
        rv.setOnClickPendingIntent(
            R.id.chip_wa,
            if (wa != null) PendingIntent.getActivity(context, 21, wa, FLAG) else intentApp(context)
        )
    }

    private fun label(status: Status): String = when (status) {
        Status.MATI -> "Mati"
        Status.SIAGA -> "Siaga"
        Status.AKTIF -> "Aktif"
    }

    private fun warna(context: Context, status: Status): Int = ContextCompat.getColor(
        context,
        when (status) {
            Status.MATI -> R.color.w_redup
            Status.SIAGA -> R.color.w_biru
            Status.AKTIF -> R.color.w_ungu
        }
    )

    private fun punyaIzin(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun aksiLayanan(context: Context, aksi: String) =
        Intent(context, LayananSiaga::class.java).setAction(aksi)

    private fun intentApp(context: Context): PendingIntent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        FLAG
    )

    private fun intentToggle(context: Context, status: Status): PendingIntent = when {
        status != Status.MATI -> PendingIntent.getService(
            context, 11, aksiLayanan(context, LayananSiaga.AKSI_MATIKAN), FLAG
        )
        punyaIzin(context) -> PendingIntent.getForegroundService(
            context, 10, aksiLayanan(context, LayananSiaga.AKSI_NYALAKAN), FLAG
        )
        else -> intentApp(context)
    }

    private fun intentOrb(context: Context, status: Status): PendingIntent = when {
        status == Status.AKTIF -> PendingIntent.getService(
            context, 12, aksiLayanan(context, LayananSiaga.AKSI_AKHIRI), FLAG
        )
        punyaIzin(context) -> PendingIntent.getForegroundService(
            context, 13, aksiLayanan(context, LayananSiaga.AKSI_PERCAKAPAN), FLAG
        )
        else -> intentApp(context)
    }
}
