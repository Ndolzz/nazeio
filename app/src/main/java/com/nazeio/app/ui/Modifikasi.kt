package com.nazeio.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composable

/**
 * Klik tanpa efek riak, untuk tombol besar minimalis.
 */
fun Modifier.clickableTanpaRiak(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = null,
        indication = null
    ) { onClick() }
}
