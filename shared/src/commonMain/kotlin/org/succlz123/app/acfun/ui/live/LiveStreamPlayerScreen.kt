package org.succlz123.app.acfun.ui.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.value
import org.succlz123.lib.screen.viewmodel.sharedViewModel
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.video.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveStreamPlayerScreen() {
    val screenNavigator = LocalScreenNavigator.current
    val screenRecord = LocalScreenRecord.current
    val id = screenRecord.arguments.value<String?>("KEY_ID")
    val title = screenRecord.arguments.value<String?>("KEY_TITLE").orEmpty()
    if (id.isNullOrEmpty()) {
        screenNavigator.pop()
    } else {
        val liveViewModel = viewModel(key = id) {
            LiveStreamViewModel()
        }
        val playerViewModel = sharedViewModel {
            VideoPlayerViewModel()
        }
        LaunchedEffect(Unit) {
            liveViewModel.getLiveStreamData(id)
        }
        val focusRequester = remember { FocusRequester() }
        SideEffect {
            focusRequester.requestFocus()
        }
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.Spacebar, Key.Enter, Key.DirectionCenter -> {
                                if (playerViewModel.videoPlayerState.value is VideoPlayerState.Playing) {
                                    playerViewModel.playerAction.value = VideoPlayerAction.Pause
                                } else {
                                    playerViewModel.playerAction.value = VideoPlayerAction.Play
                                }
                                playerViewModel.showControllerCover.value = true
                                true
                            }

                            Key.Back, Key.Escape -> {
                                screenNavigator.pop()
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    } else {
                        true
                    }
                }.focusRequester(focusRequester).focusTarget()
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
            val representations = liveViewModel.liveSteam.collectAsState().value.invoke()
            if (representations.isNullOrEmpty()) {
                LoadingView()
            } else {
                playerViewModel.playList = representations
                playerViewModel.curPlayerSource.value = representations.lastOrNull()
                println(representations.lastOrNull())
                val result = VideoPlayer(Modifier, playerViewModel)
                if (result.isNotEmpty()) {
                    screenNavigator.toast(result, time = 5000L)
                    screenNavigator.pop()
                }
                VideoPlayerCover(title, true, playerViewModel)
            }
        }
    }
}


