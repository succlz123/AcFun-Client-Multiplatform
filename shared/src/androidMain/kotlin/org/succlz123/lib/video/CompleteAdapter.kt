package org.succlz123.lib.video

import org.succlz123.hohoplayer.core.adapter.BaseAdapter
import org.succlz123.hohoplayer.core.adapter.event.PlayerAdapterKtx.requestReplay
import org.succlz123.hohoplayer.core.player.listener.OnPlayerEventListener
import org.succlz123.hohoplayer.support.message.HoHoMessage

class CompleteAdapter : BaseAdapter() {

    override val key: String
        get() = "CompleteAdapter"

    private fun setPlayCompleteState(state: Boolean) {
        if (state) {
            requestReplay()
        }
    }

    override fun onPlayerEvent(message: HoHoMessage) {
        when (message.what) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET,
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> {
                setPlayCompleteState(false)
            }

            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                setPlayCompleteState(true)
            }
        }
    }
}