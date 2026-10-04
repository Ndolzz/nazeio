package com.nazeio.app.widget

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

/**
 * Menggambar bingkai animasi untuk widget. Widget Android tidak bisa
 * menjalankan animasi sendiri, jadi layanan siaga mengganti gambar ini
 * beberapa kali per detik.
 */
object GambarWidget {

    private val BIRU = 0xFF2563EB.toInt()
    private val UNGU = 0xFF7C3AED.toInt()
    private val BIRU_MUDA = 0xFF60A5FA.toInt()
    private val UNGU_MUDA = 0xFFA78BFA.toInt()
    private val ABU_TERANG = 0xFFCBD5E1.toInt()
    private val ABU_GELAP = 0xFF64748B.toInt()
    private const val RAD = 57.2958f

    fun orb(
        gaya: String,
        status: StatusBersama.Status,
        bingkai: Int,
        ukuran: Int = 96
    ): Bitmap {
        val bmp = Bitmap.createBitmap(ukuran, ukuran, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        val pusat = ukuran / 2f
        val r = ukuran * 0.40f
        val aktif = status == StatusBersama.Status.AKTIF
        val t = bingkai * (if (aktif) 0.45f else 0.12f)

        if (aktif) {
            val d = (sin(t.toDouble()).toFloat() + 1f) / 2f
            p.style = Paint.Style.FILL
            p.color = Color.argb((70 * (1f - d)).toInt() + 10, 124, 58, 237)
            c.drawCircle(pusat, pusat, r + d * ukuran * 0.09f, p)
        }

        p.style = Paint.Style.FILL
        if (status == StatusBersama.Status.MATI) {
            p.color = ABU_TERANG
        } else {
            p.shader = LinearGradient(
                0f, 0f, ukuran.toFloat(), ukuran.toFloat(),
                if (aktif) UNGU else BIRU, if (aktif) BIRU else UNGU,
                Shader.TileMode.CLAMP
            )
        }
        c.drawCircle(pusat, pusat, r, p)
        p.shader = null

        if (status == StatusBersama.Status.MATI) {
            gambarMik(c, p, pusat, ukuran.toFloat())
            return bmp
        }

        p.color = Color.WHITE
        when (gaya) {
            "kipas" -> gambarKipas(c, p, pusat, ukuran.toFloat(), t)
            "badai" -> gambarBadai(c, p, pusat, ukuran.toFloat(), t)
            else -> gambarGelombang(c, p, pusat, ukuran.toFloat(), t)
        }
        return bmp
    }

    private fun gambarMik(c: Canvas, p: Paint, pusat: Float, s: Float) {
        p.style = Paint.Style.STROKE
        p.strokeWidth = s * 0.05f
        p.strokeCap = Paint.Cap.ROUND
        p.color = ABU_GELAP
        val w = s * 0.075f
        c.drawRoundRect(
            RectF(pusat - w, pusat - s * 0.20f, pusat + w, pusat + s * 0.04f), w, w, p
        )
        c.drawArc(
            RectF(pusat - s * 0.13f, pusat - s * 0.10f, pusat + s * 0.13f, pusat + s * 0.14f),
            0f, 180f, false, p
        )
        c.drawLine(pusat, pusat + s * 0.14f, pusat, pusat + s * 0.22f, p)
    }

    private fun gambarGelombang(c: Canvas, p: Paint, pusat: Float, s: Float, t: Float) {
        p.style = Paint.Style.FILL
        val lebar = s * 0.065f
        val jarak = s * 0.105f
        for (i in 0 until 5) {
            val tinggi = s * (0.10f + 0.26f * abs(sin((t + i * 0.9f).toDouble()).toFloat()))
            val x = pusat + (i - 2) * jarak
            c.drawRoundRect(
                RectF(x - lebar / 2, pusat - tinggi / 2, x + lebar / 2, pusat + tinggi / 2),
                lebar / 2, lebar / 2, p
            )
        }
    }

    private fun gambarKipas(c: Canvas, p: Paint, pusat: Float, s: Float, t: Float) {
        p.style = Paint.Style.FILL
        val lebar = s * 0.11f
        val panjang = s * 0.25f
        c.save()
        c.rotate(t * RAD, pusat, pusat)
        for (i in 0 until 3) {
            c.save()
            c.rotate(120f * i, pusat, pusat)
            c.drawOval(
                RectF(pusat - lebar / 2, pusat - panjang, pusat + lebar / 2, pusat - s * 0.02f), p
            )
            c.restore()
        }
        c.restore()
        p.color = UNGU
        c.drawCircle(pusat, pusat, s * 0.045f, p)
    }

    private fun gambarBadai(c: Canvas, p: Paint, pusat: Float, s: Float, t: Float) {
        p.style = Paint.Style.STROKE
        p.strokeWidth = s * 0.04f
        p.strokeCap = Paint.Cap.ROUND
        val jari = floatArrayOf(0.28f, 0.19f, 0.10f)
        val laju = floatArrayOf(1f, -1.4f, 2f)
        for (k in 0 until 3) {
            val rr = s * jari[k]
            val awal = t * RAD * laju[k]
            val kotak = RectF(pusat - rr, pusat - rr, pusat + rr, pusat + rr)
            c.drawArc(kotak, awal, 110f, false, p)
            c.drawArc(kotak, awal + 180f, 70f, false, p)
        }
        p.style = Paint.Style.FILL
        c.drawCircle(pusat, pusat, s * 0.025f, p)
    }

    /**
     * Tepi neon untuk status Siaga (berdenyut pelan) dan Aktif (berputar).
     * Mode hemat baterai menghentikan putaran dan hanya menyisakan denyut.
     */
    fun neon(
        status: StatusBersama.Status,
        bingkai: Int,
        lebar: Int,
        tinggi: Int,
        pecahRadius: Float,
        hemat: Boolean
    ): Bitmap {
        val bmp = Bitmap.createBitmap(lebar, tinggi, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val aktif = status == StatusBersama.Status.AKTIF
        val sudut = if (hemat) 0f else bingkai * (if (aktif) 14f else 2.5f)
        val napas = (sin(bingkai * (if (aktif) 0.5 else 0.25)).toFloat() + 1f) / 2f
        val alphaDasar = if (aktif) 0.75f + 0.25f * napas else 0.45f + 0.35f * napas
        val sisiPendek = min(lebar, tinggi).toFloat()
        val tebal = sisiPendek * 0.035f
        val inset = tebal * 1.6f
        val radius = pecahRadius * sisiPendek
        val kotak = RectF(inset, inset, lebar - inset, tinggi - inset)

        val shader = SweepGradient(
            lebar / 2f, tinggi / 2f,
            intArrayOf(BIRU, UNGU_MUDA, BIRU_MUDA, UNGU, BIRU), null
        )
        val m = Matrix()
        m.postRotate(sudut, lebar / 2f, tinggi / 2f)
        shader.setLocalMatrix(m)

        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.style = Paint.Style.STROKE
        p.shader = shader

        p.strokeWidth = tebal * 2.4f
        p.maskFilter = BlurMaskFilter(tebal * 1.8f, BlurMaskFilter.Blur.NORMAL)
        p.alpha = (255 * alphaDasar * 0.55f).toInt()
        c.drawRoundRect(kotak, radius, radius, p)

        p.maskFilter = null
        p.strokeWidth = tebal
        p.alpha = (255 * alphaDasar).toInt()
        c.drawRoundRect(kotak, radius, radius, p)
        return bmp
    }
}
