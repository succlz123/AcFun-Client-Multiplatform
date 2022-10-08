package org.succlz123.lib.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.base.AcBackButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.LocalScreenNavigator


@Composable
fun VideoPlayerCover(
    title: String, isLive: Boolean, playerViewModel: VideoPlayerViewModel
) {
    val screenNavigator = LocalScreenNavigator.current
    val playerState = playerViewModel.videoPlayerState.collectAsState().value
    println(playerState)

    LaunchedEffect(playerState, playerViewModel.showControllerCover.value) {
        if (playerViewModel.videoPlayerState.value !is VideoPlayerState.Init && playerViewModel.showControllerCover.value) {
            playerViewModel.startCountDown()
        }
    }

    Box(modifier = Modifier.fillMaxSize().noRippleClickable {
        playerViewModel.showControllerCover.value = !playerViewModel.showControllerCover.value
    }) {
        if (playerViewModel.showControllerCover.collectAsState().value) {
            Column(modifier = Modifier.fillMaxSize().noRippleClickable {
                playerViewModel.showControllerCover.value = !playerViewModel.showControllerCover.value
            }) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(
                        Brush.verticalGradient(colors = listOf(Color(0xcc000000), Color(0x4D000000), Color.Transparent))
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp, 48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AcBackButton(tint = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = title, style = MaterialTheme.typography.h1, color = Color.White, maxLines = 2
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (playerState !is VideoPlayerState.Init) {
                    Box(
                        modifier = Modifier.fillMaxWidth().background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x4D000000),
                                    Color(0xcc000000)
                                )
                            )
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.noRippleClickable {
                                if (playerState is VideoPlayerState.Playing) {
                                    playerViewModel.playerAction.value = VideoPlayerAction.Pause
                                } else {
                                    playerViewModel.playerAction.value = VideoPlayerAction.Play
                                }
                            }) {
                                AsyncImageUrlMultiPlatform(
                                    url = if (playerState is VideoPlayerState.Playing) {
                                        "ic_pause.png"
                                    } else {
                                        "ic_play.png"
                                    }, modifier = Modifier.width(32.dp).height(32.dp)
                                )
                            }
                            if (isLive) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                                val time = playerViewModel.time.collectAsState()
                                val duration = playerViewModel.duration.collectAsState()
                                Text(
                                    text = PlayerTimeUtil.getTimeFormat1(time.value),
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(24.dp))
                                var sliderValue = remember { 0.0f }
                                Slider(
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        inactiveTrackColor = Color.Gray,
                                        activeTrackColor = Color.White,
                                    ),
                                    value = PlayerTimeUtil.getProgress(time.value, duration.value),
                                    onValueChange = { sliderValue = it },
                                    onValueChangeFinished = {
                                        playerViewModel.playerAction.value =
                                            VideoPlayerAction.Seek((sliderValue * playerViewModel.duration.value).toLong())
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(24.dp))
                                Text(
                                    text = PlayerTimeUtil.getTimeFormat1(duration.value),
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                            Icon(
                                Icons.Sharp.Settings, modifier = Modifier.size(28.dp).noRippleClickable {
                                    screenNavigator.push(Manifest.VideoPlayerSettingScreen)
                                }, contentDescription = "Settings", tint = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (playerState is VideoPlayerState.Init || playerState is VideoPlayerState.Buffering || playerState is VideoPlayerState.Seeking) {
            LoadingView()
        }
    }
}

