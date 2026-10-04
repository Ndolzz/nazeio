package com.nazeio.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

/**
 * Klik tanpa efek riak, untuk tombol besar minimalis.
 */
fun Modifier.clickableTanpaRiak(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = null,
        indication = null
    ) { onClick() }
}
