package org.succlz123.lib.video

import org.succlz123.hohoplayer.core.adapter.bridge.Bridge
import org.succlz123.hohoplayer.core.adapter.event.PlayerAdapterKtx
import org.succlz123.hohoplayer.core.player.base.IPlayer
import org.succlz123.hohoplayer.core.source.DataSource
import org.succlz123.hohoplayer.support.message.HoHoMessage
import org.succlz123.hohoplayer.widget.videoview.VideoView
import org.succlz123.hohoplayer.widget.videoview.VideoViewAdapterEventHandler

class AndroidVideoPlayer(var videoView: VideoView) {
    private var bridge: Bridge? = null

    var isHostForeground: Boolean = false

    private var userPause = false

    private var hasStart = false

    var url: String? = null

    fun onCreate(playerState: PlayerStateAdapter) {
        bridge = Bridge().apply {
            videoView.setBridge(this)
            addAdapter(playerState)
            addAdapter(CompleteAdapter())
        }
        videoView.videoViewEventHandler = videoViewEventHandler
    }

    private val videoViewEventHandler = object : VideoViewAdapterEventHandler() {
        override fun onHandle(t: VideoView, message: HoHoMessage) {
            super.onHandle(t, message)
            when (message.what) {
                PlayerAdapterKtx.CODE_REQUEST_PAUSE -> {
                    userPause = true
                }
            }
        }

        override fun requestRetry(t: VideoView, message: HoHoMessage) {
            if (isHostForeground) {
                super.requestRetry(t, message)
            }
        }
    }

    private fun initPlayAfterResume() {
        if (!hasStart) {
            val dataSource = DataSource("", url.orEmpty())
            videoView.setDataSource(dataSource)
            videoView.start()
            hasStart = true
        }
    }

    fun onPause() {
        val state = videoView.getState()
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE) {
            return
        }
        if (videoView.isInPlaybackState()) {
            videoView.pause()
        } else {
            videoView.stop()
        }
    }

    fun onResume() {
        videoView.requestFocus()
        val state = videoView.getState()
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE) {
            return
        }
        if (videoView.isInPlaybackState()) {
            if (!userPause) {
                videoView.resume()
            }
        } else {
            videoView.rePlay(0)
        }
        initPlayAfterResume()
    }

    fun onDestroy() {
        videoView.stopPlayback()
    }
}
