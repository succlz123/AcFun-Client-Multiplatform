package org.succlz123.lib.video

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.api.bean.Representation
import org.succlz123.app.acfun.danmaku.DanmakuBean
import org.succlz123.lib.screen.viewmodel.ScreenViewModel

class VideoPlayerViewModel : ScreenViewModel() {

    var playList: List<Representation>? = null

    val curPlayerSource: MutableStateFlow<Representation?> = MutableStateFlow(null)

    val curPlayerLocalSource: MutableStateFlow<String> = MutableStateFlow("")

    val playerSetting = PlayerSetting()

    val playerAction: MutableStateFlow<VideoPlayerAction> = MutableStateFlow(VideoPlayerAction.Init)

    val videoPlayerState: MutableStateFlow<VideoPlayerState> = MutableStateFlow(VideoPlayerState.Init)

    val time: MutableStateFlow<Long> = MutableStateFlow(0L)

    val duration: MutableStateFlow<Long> = MutableStateFlow(0L)

    val currentWorkDanmaku: MutableStateFlow<MutableList<DanmakuBean>> = MutableStateFlow(arrayListOf())

    val showControllerCover = MutableStateFlow(true)

    private var curRunnableJob: Job? = null

    fun startCountDown() {
        curRunnableJob?.cancel()
        curRunnableJob = viewModelScope.launch {
            delay(5000L)
            showControllerCover.value = false
        }
    }
}
