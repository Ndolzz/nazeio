package com.nazeio.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

abstract class DasarWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        PembaruWidget.penuh(context)
    }

    override fun onEnabled(context: Context) {
        PembaruWidget.penuh(context)
    }
}

/** Dua kali dua. */
class WidgetKecil : DasarWidget()

/** Empat kali satu. */
class WidgetPanjang : DasarWidget()

/** Empat kali dua. */
class WidgetBesar : DasarWidget()
