package org.succlz123.lib.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    modifier: Modifier = Modifier, playerViewModel: VideoPlayerViewModel
): String