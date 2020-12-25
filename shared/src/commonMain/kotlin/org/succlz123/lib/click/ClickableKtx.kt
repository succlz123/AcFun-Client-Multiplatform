package org.succlz123.lib.click

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun Modifier.soundClick(onClick: () -> Unit): Modifier = composed {
    val soundInteraction = remember {
//        val mediaPlayer = CallbackMediaPlayerComponent().mediaPlayer()
//        mediaPlayer.media()?.prepare("/Users/succlz123/Downloads/android_assets_sound_se_item00.wav")
        val interaction = object : MutableInteractionSource {

            override val interactions = MutableSharedFlow<Interaction>(
                extraBufferCapacity = 16,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
            )

            override suspend fun emit(interaction: Interaction) {
                interactions.emit(interaction)
                if (interaction is HoverInteraction.Enter) {
//                    mediaPlayer.controls().play()
                }
            }

            override fun tryEmit(interaction: Interaction): Boolean {
                return interactions.tryEmit(interaction)
            }
        }
        interaction
    }
    clickable(indication = null, interactionSource = soundInteraction) {
        onClick()
    }
}