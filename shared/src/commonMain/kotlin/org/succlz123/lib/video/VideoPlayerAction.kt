package org.succlz123.lib.video

sealed class VideoPlayerAction(var seekTime: Long = -1) {

    object Init : VideoPlayerAction()

    object Play : VideoPlayerAction()

    object Pause : VideoPlayerAction()

    object Stop : VideoPlayerAction()

    object RePlay : VideoPlayerState()

    class Seek(seekTime: Long) : VideoPlayerAction(seekTime)
}
