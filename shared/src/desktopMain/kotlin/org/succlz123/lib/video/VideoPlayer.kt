package org.succlz123.lib.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.succlz123.lib.video.vlc.rememberMediaPlayer
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter

@Composable
actual fun VideoPlayer(modifier: Modifier, playerViewModel: VideoPlayerViewModel): String {
    //    VideoPlayerImpl(playerViewModel.playList?.firstOrNull()?.url.orEmpty())
    val hasVlc = NativeDiscovery().discover()
    if (!hasVlc) {
        return "桌面客户端视频播放依赖 VLC 组件，请前往 https://www.videolan.org/ 下载并安装。"
    }
    VideoPlayerVlc(playerViewModel)
    return ""
}

@Composable
fun VideoPlayerVlc(playerViewModel: VideoPlayerViewModel) {
    val imageBitmapState = remember { mutableStateOf<ImageBitmap?>(null) }
    imageBitmapState.value?.let {
        Image(bitmap = it, contentDescription = "Video", modifier = Modifier.fillMaxSize())
    } ?: run {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
    }
    val mediaPlayerWrapper = rememberMediaPlayer(imageBitmapState)
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(mediaPlayerWrapper.mediaPlayer.value, playerViewModel.curPlayerSource.value) {
        val mediaPlayer = mediaPlayerWrapper.mediaPlayer.value
        val currentContext = coroutineContext
        if (mediaPlayer == null) {
            launch(Dispatchers.IO) {
                val newPlayer = mediaPlayerWrapper.init()
                launch(currentContext) {
                    if (newPlayer != null) {
                        mediaPlayerWrapper.mediaPlayer.value = newPlayer
                    }
                }
            }
        } else {
            mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {

                override fun mediaPlayerReady(mediaPlayer: MediaPlayer) {
                    super.mediaPlayerReady(mediaPlayer)
                    playerViewModel.duration.value = mediaPlayer.status().length()
                    mediaPlayer.controls().start()
                }

                override fun finished(mediaPlayer: MediaPlayer) {
                    super.finished(mediaPlayer)
                    mediaPlayer.controls().pause()
                }

                override fun timeChanged(mediaPlayer: MediaPlayer, newTime: Long) {
                    super.timeChanged(mediaPlayer, newTime)
                    playerViewModel.time.value = newTime
                }

                override fun opening(mediaPlayer: MediaPlayer?) {
                    super.opening(mediaPlayer)
                    playerViewModel.videoPlayerState.value = VideoPlayerState.Buffering()
                }

                override fun buffering(mediaPlayer: MediaPlayer?, newCache: Float) {
                    super.buffering(mediaPlayer, newCache)
                    if (newCache <= 95) {
                        playerViewModel.videoPlayerState.value = VideoPlayerState.Buffering(newCache)
                    } else {
                        playerViewModel.videoPlayerState.value = VideoPlayerState.Playing
                    }
                }

                override fun playing(mediaPlayer: MediaPlayer?) {
                    super.playing(mediaPlayer)
                    playerViewModel.videoPlayerState.value = VideoPlayerState.Playing
                }

                override fun paused(mediaPlayer: MediaPlayer?) {
                    super.paused(mediaPlayer)
                    playerViewModel.videoPlayerState.value = VideoPlayerState.Pause
                }

                override fun stopped(mediaPlayer: MediaPlayer?) {
                    super.stopped(mediaPlayer)
                    playerViewModel.videoPlayerState.value = VideoPlayerState.Stop
                }

                override fun seekableChanged(mediaPlayer: MediaPlayer?, newSeekable: Int) {
                    super.seekableChanged(mediaPlayer, newSeekable)
                    playerViewModel.videoPlayerState.value = VideoPlayerState.Stop
                }
            })
            coroutineScope.launch {
                playerViewModel.playerSetting.speed.collect {
                    mediaPlayer.controls()?.setRate(it.toFloat())
                }
            }
            coroutineScope.launch {
                playerViewModel.playerAction.collect {
                    when (it) {
                        VideoPlayerAction.Init -> {
                            val source = playerViewModel.curPlayerSource.value
                            val localSource = playerViewModel.curPlayerLocalSource.value
                            if (localSource.isNotEmpty()) {
                                mediaPlayer.media().play(localSource)
                            } else if (source != null && !source.url.isNullOrEmpty()) {
                                mediaPlayer.media().play(source.url)
                            }
                        }

                        VideoPlayerAction.Pause -> {
                            mediaPlayer.controls().pause()
                        }

                        VideoPlayerAction.Play -> {
                            mediaPlayer.controls().play()
                        }

                        is VideoPlayerAction.Seek -> {
                            mediaPlayer.controls().setTime(it.seekTime)
                        }

                        VideoPlayerAction.Stop -> {
                            mediaPlayer.controls().stop()
                        }
                    }
                }
            }
        }
    }
    DisposableEffect(Unit, effect = {
        onDispose {
            mediaPlayerWrapper.mediaPlayer.value?.release()
        }
    })
}
