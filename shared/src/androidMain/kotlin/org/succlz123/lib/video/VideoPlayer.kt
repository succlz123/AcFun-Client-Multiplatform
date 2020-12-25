package org.succlz123.lib.video

import android.view.LayoutInflater
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import org.succlz123.hohoplayer.core.render.AspectRatio
import org.succlz123.hohoplayer.widget.videoview.VideoView
import org.succlz123.lib.acfun.R
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.ScreenLifecycleCallback
import org.succlz123.lib.screen.lifecycle.ScreenLifecycle

@Composable
actual fun VideoPlayer(modifier: Modifier, playerViewModel: VideoPlayerViewModel): String {
    val screenRecord = LocalScreenRecord.current
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { mutableStateOf(ScreenLifecycle.State.UNKNOWN) }
    val androidVideoPlayer = remember { mutableStateOf<AndroidVideoPlayer?>(null) }
    ScreenLifecycleCallback(screenRecord, screenLifecycleState = {
        screenState.value = it
    })
    val curPlaySource = playerViewModel.curPlayerSource.collectAsState().value?.url

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            playerViewModel.playerSetting.speed.collect {
                val player = androidVideoPlayer.value ?: return@collect
                player.videoView.setSpeed(it.toFloat())
            }
        }
        coroutineScope.launch {
            playerViewModel.playerSetting.ratio.collect {
                val player = androidVideoPlayer.value ?: return@collect
                player.videoView.setAspectRatio(
                    when (it) {
                        "4:3" -> {
                            AspectRatio.AspectRatio_4_3
                        }

                        "16:9" -> {
                            AspectRatio.AspectRatio_16_9
                        }

                        "填充" -> {
                            AspectRatio.AspectRatio_FILL_PARENT
                        }

                        "原始" -> {
                            AspectRatio.AspectRatio_ORIGIN
                        }

                        "适应" -> {
                            AspectRatio.AspectRatio_FIT_PARENT
                        }

                        else -> {
                            AspectRatio.AspectRatio_FIT_PARENT
                        }
                    }
                )
            }
        }
        coroutineScope.launch {
            playerViewModel.playerAction.collect {
                val player = androidVideoPlayer.value ?: return@collect
                when (it) {
                    VideoPlayerAction.Init -> {
                        val source = playerViewModel.curPlayerSource.value
                        if (source != null && !source.url.isNullOrEmpty()) {
//                            mediaPlayer.media().play(source.url)
                        }
                    }

                    VideoPlayerAction.Pause -> {
                        player.videoView.pause()
                    }

                    VideoPlayerAction.Play -> {
                        player.videoView.resume()
                    }

                    is VideoPlayerAction.Seek -> {
                        player.videoView.seekTo(it.seekTime.toInt())
                    }

                    VideoPlayerAction.Stop -> {
                        player.videoView.stop()
                    }
                }
            }
        }
    }
    AndroidView(factory = {
        LayoutInflater.from(it).inflate(R.layout.activity_player, null).apply {
            val videoView = findViewById<VideoView>(R.id.videoView)
            androidVideoPlayer.value = AndroidVideoPlayer(videoView).apply {
                val playerState = PlayerStateAdapter(
                    playerViewModel.videoPlayerState, playerViewModel.time, playerViewModel.duration
                )
                onCreate(playerState)
            }
        }
    }, modifier = modifier.fillMaxSize()) {
        val player = androidVideoPlayer.value
        if (player != null) {
            player.url = curPlaySource
            when (screenState.value) {
                ScreenLifecycle.State.UNKNOWN -> {

                }

                ScreenLifecycle.State.DESTROYED -> {
                    player.onDestroy()
                }

                ScreenLifecycle.State.CREATED -> {
                }

                ScreenLifecycle.State.PAUSED -> {
                    player.isHostForeground = false
                    player.onPause()
                }

                ScreenLifecycle.State.RESUMED -> {
                    player.isHostForeground = true
                    player.onResume()
                }
            }
        }
    }
    return ""
}