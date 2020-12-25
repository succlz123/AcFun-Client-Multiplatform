package org.succlz123.lib.video

import kotlinx.coroutines.flow.MutableStateFlow
import org.succlz123.hohoplayer.core.adapter.BaseAdapter
import org.succlz123.hohoplayer.core.player.listener.OnPlayerEventListener
import org.succlz123.hohoplayer.core.player.time.OnTimerUpdateListener
import org.succlz123.hohoplayer.support.message.HoHoMessage

class PlayerStateAdapter(
    val videoPlayerState: MutableStateFlow<VideoPlayerState>,
    val timeState: MutableStateFlow<Long>,
    val durationState: MutableStateFlow<Long>
) : BaseAdapter(), OnTimerUpdateListener {

    override val key: String
        get() = "PlayerStateAdapter"

    override fun onTimerUpdate(curr: Int, duration: Int, bufferPercentage: Int) {
        timeState.value = curr.toLong()
        durationState.value = duration.toLong()
    }

    override fun onPlayerEvent(message: HoHoMessage) {
        when (message.what) {
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET,
            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO -> {
                videoPlayerState.value = VideoPlayerState.Buffering()
            }

            OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE -> {
                videoPlayerState.value = VideoPlayerState.Pause
            }

            OnPlayerEventListener.PLAYER_EVENT_ON_STOP,
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                videoPlayerState.value = VideoPlayerState.Stop
            }

            OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR -> {
                videoPlayerState.value = VideoPlayerState.Error
            }

            OnPlayerEventListener.PLAYER_EVENT_ON_RESUME,
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START,
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END,
            OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> {
                videoPlayerState.value = VideoPlayerState.Playing
            }
        }
    }

    override fun onErrorEvent(message: HoHoMessage) {
        videoPlayerState.value = VideoPlayerState.Error
    }
}