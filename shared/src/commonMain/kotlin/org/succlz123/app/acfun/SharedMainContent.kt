package org.succlz123.app.acfun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.AcFunTheme
import org.succlz123.app.acfun.ui.area.AreaContentScreen
import org.succlz123.app.acfun.ui.detail.VideoDetailScreen
import org.succlz123.app.acfun.ui.live.LiveStreamPlayerScreen
import org.succlz123.app.acfun.ui.main.MainScreen
import org.succlz123.app.acfun.ui.user.UserSpaceScreen
import org.succlz123.app.acfun.ui.video.VideoPlayerScreen
import org.succlz123.app.acfun.ui.video.VideoPlayerSettingDialog
import org.succlz123.lib.fps.FpsMonitor
import org.succlz123.lib.screen.ScreenHost
import org.succlz123.lib.screen.rememberScreenNavigator

object AppBuildConfig {
    var isDebug = false
}

object Manifest {
    const val MainScreen = "MainScreen"
    const val VideoDetailScreen = "VideoDetailScreen"
    const val VideoPlayerScreen = "VideoPlayerScreen"
    const val LiveStreamPlayerScreen = "LiveStreamPlayerScreen"
    const val VideoPlayerSettingScreen = "VideoPlayerSettingScreen"
    const val UserSpaceScreen = "UserSpaceScreen"
    const val AreaContentScreen = "AreaContentScreen"
}

@Composable
fun SharedMainContent() {
    val screenNavigator = rememberScreenNavigator()
    AcFunTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            ScreenHost(screenNavigator = screenNavigator, rootScreenName = Manifest.MainScreen) {
                groupScreen(screenName = (Manifest.MainScreen)) {
                    MainScreen()
                }
                groupScreen(screenName = (Manifest.VideoDetailScreen)) {
                    VideoDetailScreen()
                }
                groupScreen(screenName = (Manifest.VideoPlayerScreen)) {
                    VideoPlayerScreen()
                }
                groupScreen(screenName = (Manifest.LiveStreamPlayerScreen)) {
                    LiveStreamPlayerScreen()
                }
                itemScreen(screenName = (Manifest.VideoPlayerSettingScreen)) {
                    VideoPlayerSettingDialog()
                }
                groupScreen(screenName = (Manifest.UserSpaceScreen)) {
                    UserSpaceScreen()
                }
                groupScreen(screenName = (Manifest.AreaContentScreen)) {
                    AreaContentScreen()
                }
            }
            if (AppBuildConfig.isDebug) {
                FpsMonitor(modifier = Modifier.padding(32.dp, 128.dp))
            }
        }
    }
}
